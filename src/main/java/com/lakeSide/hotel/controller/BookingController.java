package com.lakeSide.hotel.controller;

import com.lakeSide.hotel.exception.InvalidBookingRequestException;
import com.lakeSide.hotel.exception.ResourceNotFoundException;
import com.lakeSide.hotel.model.BookedRoom;
import com.lakeSide.hotel.model.Room;
import com.lakeSide.hotel.response.BookingResponse;
import com.lakeSide.hotel.response.RoomResponse;
import com.lakeSide.hotel.service.BookingService;
import com.lakeSide.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;
    private final RoomService roomService;

    @GetMapping("/room/allBookings")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<BookingResponse>> getAllBookings(){
        try {
            List<BookedRoom> bookings = bookingService.getAllBookings();
            List<BookingResponse> bookingResponses = new ArrayList<>();
            for (BookedRoom booking : bookings){
                BookingResponse bookingResponse = getBookingResponse(booking);
                bookingResponses.add(bookingResponse);
            }
            return ResponseEntity.ok(bookingResponses);
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId, @RequestBody BookedRoom bookingRequest){
        try {
            String confirmationCode = bookingService.saveBooking(roomId,bookingRequest);
            return ResponseEntity.ok("Your Booking has been confirmed successfully! Your confirmation code is "+ confirmationCode);
        }catch (InvalidBookingRequestException e){
            return ResponseEntity.badRequest().body("Booking request canceled");
        }
    }

    @DeleteMapping("/room/{bookingId}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId){
        return bookingService.cancelBooking(bookingId);
    }

    @PutMapping("/room/update/{roomId}/{bookingId}/booking")
    public ResponseEntity<?> updateBooking(@PathVariable Long roomId,@PathVariable Long bookingId, @RequestBody BookedRoom bookingRequest){
        try{
            String confirmationCode = bookingService.updateBooking(roomId,bookingId, bookingRequest);
            return ResponseEntity.ok("Your Booking has been updated successfully! Your confirmation code is " + confirmationCode);
        }catch (InvalidBookingRequestException e){
            return ResponseEntity.badRequest().body("Booking request cannot be updated");
        }
    }

    @GetMapping("/confirmation/{confirmationCode}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode){
        try{
            BookedRoom booking = bookingService.findBookingByConfirmationCode(confirmationCode);
            BookingResponse bookingResponse = getBookingResponse(booking);
            return ResponseEntity.ok(bookingResponse);
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking cannot be found");
        }
    }

    @GetMapping("/user/{guestEmail}/bookings")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<BookingResponse>> getBookingByUserEmail(@PathVariable String guestEmail){
        try {
            List<BookedRoom> bookings = bookingService.findBookingByGuestEmail(guestEmail);
            List<BookingResponse> bookingResponses = new ArrayList<>();
            for (BookedRoom booking: bookings){
                BookingResponse bookingResponse = getBookingResponse(booking);
                bookingResponses.add(bookingResponse);
            }
            return ResponseEntity.ok(bookingResponses);
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    private BookingResponse getBookingResponse(BookedRoom booking) {
        Room theRoom = roomService.getRoomById(booking.getRoom().getId()).get();
        RoomResponse room = new RoomResponse(
                theRoom.getId(),
                theRoom.getRoomType(),
                theRoom.getRoomPrice()
        );
        BookingResponse bookingResponse = new BookingResponse(
                booking.getBookingId(),
                booking.getCheckInDate(), booking.getCheckOutDate(),
                booking.getGuestName(), booking.getGuestEmail(),
                booking.getNumOfChildren(), booking.getNumOfChildren(),
                booking.getTotalNumOfGuests(),
                booking.getBookingConfirmationCode(),
                room
        );
        return bookingResponse;
    }
}
