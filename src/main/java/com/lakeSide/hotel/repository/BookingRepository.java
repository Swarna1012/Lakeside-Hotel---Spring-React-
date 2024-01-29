package com.lakeSide.hotel.repository;

import com.lakeSide.hotel.model.BookedRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookedRoom, Long> {
    List<BookedRoom> findByRoomId(Long roomId);

    ResponseEntity<Void> deleteByBookingId(Long bookingId);

    BookedRoom findByBookingConfirmationCode(String confirmationCode);

    List<BookedRoom> findBookingByGuestEmail(String guestEmail);
}
