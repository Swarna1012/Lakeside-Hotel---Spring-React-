package com.lakeSide.hotel.controller;

import com.lakeSide.hotel.exception.PhotoRetrievalException;
import com.lakeSide.hotel.exception.ResourceNotFoundException;
import com.lakeSide.hotel.model.BookedRoom;
import com.lakeSide.hotel.model.Ratings;
import com.lakeSide.hotel.model.Room;
import com.lakeSide.hotel.response.BookingResponse;
import com.lakeSide.hotel.response.RatingResponses;
import com.lakeSide.hotel.response.RoomResponse;
import com.lakeSide.hotel.service.BookingService;
import com.lakeSide.hotel.service.RatingsService;
import com.lakeSide.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final BookingService bookingService;
    private final RatingsService ratingsService;

    @PostMapping("/add/new-room")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomResponse> addNewRoom(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") BigDecimal roomPrice) throws SQLException, IOException {
        Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice);
        RoomResponse response = new RoomResponse(savedRoom.getId(), savedRoom.getRoomType(), savedRoom.getRoomPrice());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/room/types")
    public List<String> getRoomTypes(){
        return roomService.getRoomTypes();
    }

    @GetMapping("/allRoomDetails")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room :rooms){
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if(photoBytes != null && photoBytes.length > 0){
//                String base64Photo = Base64.encodeBase64String(photoBytes);
                RoomResponse roomResponse = getRoomResponse(room);
//                roomResponse.setPhoto(base64Photo);
                roomResponses.add(roomResponse);
            }
        }
        return ResponseEntity.ok(roomResponses);
    }

    @DeleteMapping("/room/delete/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId){
        return roomService.deleteRoom(roomId);
    }

    @PutMapping("/room/update/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long roomId,
                                                   @RequestParam(required = false) String roomType,
                                                   @RequestParam(required = false) BigDecimal roomPrice,
                                                   @RequestParam(required = false) MultipartFile photo) throws SQLException, IOException {

        byte[] photoBytes = photo != null && !photo.isEmpty() ? photo.getBytes() : roomService.getRoomPhotoByRoomId(roomId);
        Blob photoBlob = photoBytes != null ? new SerialBlob(photoBytes) : null;

        Room theRoom = roomService.updateRoom(roomId, roomType, roomPrice, photoBytes);
        theRoom.setPhoto(photoBlob);
        RoomResponse roomResponse = getRoomResponse(theRoom);
        return ResponseEntity.ok(roomResponse);
    }

    @GetMapping("/roomDetails/{roomId}")
    @Transactional
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable Long roomId){
        Optional<Room> theRoom = roomService.getRoomById(roomId);
        return theRoom.map(room -> {
            RoomResponse roomResponse = getRoomResponse(room);
            return ResponseEntity.ok(Optional.of(roomResponse));
        }).orElseThrow(() -> new  ResourceNotFoundException("Room not found!"));
    }


    @GetMapping("/allRooms")
    @Transactional
    public ResponseEntity<List<RoomResponse>> getAllRoomInfo(){
        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room: rooms){
            RoomResponse roomResponse = getRoomRatingResponses(room);
            roomResponses.add(roomResponse);
        }
        return ResponseEntity.ok(roomResponses);
    }

    private RoomResponse getRoomRatingResponses(Room room){
        byte[] photoBytes = null;
        Blob photoBlob = room.getPhoto();
        try{
            if (photoBlob != null){
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            }
        } catch (SQLException e) {
            throw new PhotoRetrievalException("Error retrieving photo...");
        }

        List<Ratings> ratings = getAllRatingsByRoomId(room.getId());
        List<RatingResponses> ratingResponses = ratings.stream()
                .map(ratings1 -> new RatingResponses(
                        ratings1.getId(),
                        ratings1.getRating(),
                        ratings1.getReview(),
                        ratings1.getGuestName()
                )).toList();
        return new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice(), room.getStars(), photoBytes, ratingResponses);
    }

    private RoomResponse getRoomResponse(Room room) throws PhotoRetrievalException {
        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
        List<BookingResponse> bookingInfo = bookings.stream()
                .map(booking -> new BookingResponse(
                        booking.getBookingId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getBookingConfirmationCode()
                )).toList();
        byte[] photoByte = null;
        Blob photoBlob = room.getPhoto();
        if(photoBlob != null){
            try {
                photoByte = photoBlob.getBytes(1, (int) photoBlob.length());
            }catch (SQLException e){
                throw new PhotoRetrievalException("Error retrieving photo...");
            }
        }


        List<Ratings> ratings = getAllRatingsByRoomId(room.getId());
        List<RatingResponses> ratingInfo = ratings.stream()
                .map(ratings1 -> new RatingResponses(
                        ratings1.getId(),
                        ratings1.getRating(),
                        ratings1.getReview(),
                        ratings1.getGuestName()
                )).toList();
        return new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice(), room.isBooked(), photoByte, bookingInfo, ratingInfo);
    }


    private List<Ratings> getAllRatingsByRoomId(Long roomId) {
        return ratingsService.getAllRatingsByRoomId(roomId);
    }

    private List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingService.getAllBookingsByRoomId(roomId);
    }

//    @GetMapping("/available")
//    public List<String> getAvailable(){
//        return roomService.getAvailable();
//    }


    @GetMapping("/filter")
    @Transactional
    public ResponseEntity<List<RoomResponse>> getFilteredRooms(
            @RequestParam("checkInDate") LocalDate checkInDate,
            @RequestParam("checkOutDate") LocalDate checkOutDate,
            @RequestParam("roomType") String roomType,
            @RequestParam("minPrice") BigDecimal minPrice,
            @RequestParam("maxPrice") BigDecimal maxPrice) throws SQLException {
        List<Room> filteredRooms = roomService.getFilteredRooms(checkInDate, checkOutDate, roomType, minPrice, maxPrice);

        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room: filteredRooms){
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0){
                roomResponses.add(new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice(), photoBytes));
            }
        }
        return ResponseEntity.ok(roomResponses);
    }

    @GetMapping("/room-calendar")
    public ResponseEntity<Map<LocalDate, Boolean>> getAvailableDate(
            @RequestParam("roomId") Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        Map availabilityDates = roomService.getAvailableDate(roomId, startDate, endDate);
        return ResponseEntity.ok(availabilityDates);
    }
}
