package com.lakeSide.hotel.service.impl;

import com.lakeSide.hotel.exception.InternalServerException;
import com.lakeSide.hotel.exception.ResourceNotFoundException;
import com.lakeSide.hotel.model.BookedRoom;
import com.lakeSide.hotel.model.Room;
import com.lakeSide.hotel.repository.RoomRepository;
import com.lakeSide.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Service
public class RoomServiceIMPL implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public Room addNewRoom(MultipartFile file, String roomType, BigDecimal roomPrice) throws IOException, SQLException {
        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);
        if (!file.isEmpty()){
            byte[] photoBytes = file.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            room.setPhoto(photoBlob);
        }
        return roomRepository.save(room);
    }

    @Override
    public List<String> getRoomTypes() {
        return roomRepository.findDistinctRoomType();
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public ResponseEntity<Void> deleteRoom(Long roomId) {
        roomRepository.deleteById(roomId);
        return null;
    }

    @Override
    public Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes){
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found!"));
        if (roomType != null) room.setRoomType(roomType);
        if (roomPrice != null) room.setRoomPrice(roomPrice);

        try {
            if(photoBytes != null && photoBytes.length>0){
                Blob photoBlob = new SerialBlob(photoBytes);
                room.setPhoto(photoBlob);
            }
        }catch (SQLException ex){
            throw new InternalServerException("Error updating room images...");
        }

        roomRepository.save(room);
        return room;
    }

    @Override
    public Optional<Room> getRoomById(Long roomId) {
        return Optional.of(roomRepository.findById(roomId)).get();
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException {
        Optional<Room> theRoom = roomRepository.findById(roomId);
        if(theRoom.isEmpty()){
            throw new ResourceNotFoundException("Sorry, Room was not found!");
        }
        Blob photoBlob = theRoom.get().getPhoto();
        if(photoBlob != null){
            return photoBlob.getBytes(1, (int) photoBlob.length());
        }
        return null;
    }

//    @Override
//    public List<String> getAvailable() {
//        List<Room> rooms = roomRepository.findByIsBookedFalse();
//        List<String> roomList = new ArrayList<>();
//        for (Room room: rooms){
//            roomList.add(room.getRoomType());
//        }
//        return roomList;
//    }


    @Override
    public List<Room> getFilteredRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType, BigDecimal minPrice, BigDecimal maxPrice) {

        List<Room> filteredRooms = new ArrayList<>();
        BookedRoom bookingRequest = new BookedRoom();
        bookingRequest.setCheckInDate(checkInDate);
        bookingRequest.setCheckOutDate(checkOutDate);

        if (roomType.equalsIgnoreCase("ALL")){
            List<Room> allRooms = roomRepository.findAll();
            for (Room room : allRooms){
                List<BookedRoom> existingBooking = room.getBookings();
                Boolean roomIsAvailable = roomIsAvailable(bookingRequest, existingBooking);
                System.out.println(roomIsAvailable);
                if (roomIsAvailable && room.getRoomPrice().compareTo(minPrice) >= 0 && room.getRoomPrice().compareTo(maxPrice) <= 0){
                    filteredRooms.add(room);
                }
            }
            System.out.println("error for filtering all rooms");
            return filteredRooms;
        }
        else{
            List<Room> allRooms = roomRepository.findByRoomTypeIgnoreCaseContaining(roomType);
            for (Room room : allRooms){
                List<BookedRoom> existingBooking = room.getBookings();
                Boolean roomIsAvailable = roomIsAvailable(bookingRequest, existingBooking);
                System.out.println(roomIsAvailable);
                if (roomIsAvailable && room.getRoomPrice().compareTo(minPrice) >= 0 && room.getRoomPrice().compareTo(maxPrice) <= 0){
                    filteredRooms.add(room);
                }
            }
            System.out.println("error for filtering particular rooms");
            return filteredRooms;
        }
    }

    @Override
    public Map getAvailableDate(Long roomId, LocalDate startDate, LocalDate endDate) {
        System.out.println("checking available dates ");
        Room room = getRoomById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room Not Found! "));
        List<BookedRoom> existingBookings = room.getBookings();

        Map<LocalDate, Boolean> availabilityRooms = new HashMap<>();
        while (!startDate.isAfter(endDate)){
            BookedRoom bookingRequest = new BookedRoom();
            bookingRequest.setCheckInDate(startDate);
            bookingRequest.setCheckOutDate(startDate.plusDays(0));

            Boolean isAvailable = roomIsAvailable(bookingRequest, existingBookings);
            availabilityRooms.put(startDate, isAvailable);

            startDate = startDate.plusDays(1);
        }
        return availabilityRooms;
    }


    public Boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
        for(BookedRoom eachBooking: existingBookings){
//            System.out.println(eachBooking.getCheckInDate() + "  " + bookingRequest.getCheckInDate());
            if (bookingRequest.getCheckInDate().equals(eachBooking.getCheckInDate()) || bookingRequest.getCheckInDate().equals(eachBooking.getCheckOutDate())){
                System.out.println("1st if");
                return false;
            }
            else if (bookingRequest.getCheckOutDate().equals(eachBooking.getCheckInDate()) || bookingRequest.getCheckOutDate().equals(eachBooking.getCheckOutDate())) {
                System.out.println("2nd if");
                return false;
            }
            else if (bookingRequest.getCheckInDate().isAfter(eachBooking.getCheckInDate()) &&
                    bookingRequest.getCheckInDate().isBefore(eachBooking.getCheckOutDate())) {
                System.out.println("3rd if");
                return false;
            }
            else if (bookingRequest.getCheckOutDate().isAfter(eachBooking.getCheckInDate()) &&
                    bookingRequest.getCheckOutDate().isBefore(eachBooking.getCheckOutDate())) {
                System.out.println("4th if");
                return false;
            }
        }
        return true;
    }
}
