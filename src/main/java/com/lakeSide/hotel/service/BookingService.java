package com.lakeSide.hotel.service;

import com.lakeSide.hotel.model.BookedRoom;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BookingService {

    List<BookedRoom> getAllBookings();

    List<BookedRoom> getAllBookingsByRoomId(Long roomId);

    String saveBooking(Long roomId, BookedRoom bookingRequest);

    ResponseEntity<Void> cancelBooking(Long bookingId);

    String updateBooking(Long roomId, Long bookingId, BookedRoom bookingRequest);

    BookedRoom findBookingByConfirmationCode(String confirmationCode);

    List<BookedRoom> findBookingByGuestEmail(String guestEmail);

}
