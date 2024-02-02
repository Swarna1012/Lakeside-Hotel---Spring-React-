package com.lakeSide.hotel.service.impl;

import com.lakeSide.hotel.exception.InvalidBookingRequestException;
import com.lakeSide.hotel.model.BookedRoom;
import com.lakeSide.hotel.model.EmailDetails;
import com.lakeSide.hotel.model.Room;
import com.lakeSide.hotel.repository.BookingRepository;
import com.lakeSide.hotel.service.BookingService;
import com.lakeSide.hotel.service.EmailService;
import com.lakeSide.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BookingServiceIMPL implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomService roomService;
    private final EmailService emailService;

    @Override
    public List<BookedRoom> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
            throw new InvalidBookingRequestException("Check-in date must be before check-out date");
        }

        Room room = roomService.getRoomById(roomId).get();
        List<BookedRoom> existingBookings = room.getBookings();
        Boolean roomIsAvailable = roomIsAvailable(bookingRequest, existingBookings);
        if (roomIsAvailable){
            room.addBooking(bookingRequest);
            bookingRepository.save(bookingRequest);
        }
        else{
            throw new InvalidBookingRequestException("This Room cannot be booked right now!");
        }

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(bookingRequest.getGuestEmail());
        emailDetails.setSubject("Booking Confirmation");
        emailDetails.setMsgBody("Your Booking has been confirmed successfully! Your confirmation code is "+ bookingRequest.getBookingConfirmationCode());
        emailService.sendSimpleEmail(emailDetails);

        return bookingRequest.getBookingConfirmationCode();
    }

    @Override
    public ResponseEntity<Void> cancelBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
        return null;
    }

    @Override
    public String updateBooking(Long roomId, Long bookingId, BookedRoom bookingRequest) {
        BookedRoom bookedRoom = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new InvalidBookingRequestException("Room not found"));

        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
            System.out.println("Check-in date must be before check-out date");
            throw new InvalidBookingRequestException("Check-in date must be before check-out date");
        }

        Room room = roomService.getRoomById(roomId).get();
        List<BookedRoom> allExistingBookings = room.getBookings();
        List<BookedRoom> existingBookings = new ArrayList<>();
        for (BookedRoom bookedRoom1: allExistingBookings){
            if(!(bookedRoom1.getBookingId().equals(bookingId))){
                existingBookings.add(bookedRoom1);
                System.out.println(bookedRoom1.getBookingId());
            }
        }

        Boolean roomIsAvailable = roomIsAvailable(bookingRequest, existingBookings);
        if (roomIsAvailable){
            room.addBooking(bookedRoom);
            bookedRoom.setNumOfChildren(bookingRequest.getNumOfChildren());
            bookedRoom.setNumOfAdults(bookingRequest.getNumOfAdults());
            bookedRoom.setGuestName(bookingRequest.getGuestName());
            bookedRoom.setGuestEmail(bookingRequest.getGuestEmail());
            bookedRoom.setCheckInDate(bookingRequest.getCheckInDate());
            bookedRoom.setCheckOutDate(bookingRequest.getCheckOutDate());
            bookingRepository.save(bookedRoom);
        }
        else{
            System.out.println("This Room cannot be booked right now!");
            throw new InvalidBookingRequestException("This Room cannot be booked right now!");
        }
        return bookedRoom.getBookingConfirmationCode();
    }

    @Override
    public BookedRoom findBookingByConfirmationCode(String confirmationCode) {
        return bookingRepository.findByBookingConfirmationCode(confirmationCode);
    }

    @Override
    public List<BookedRoom> findBookingByGuestEmail(String guestEmail) {
        return bookingRepository.findBookingByGuestEmail(guestEmail);
    }

    public Boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
        for(BookedRoom eachBooking: existingBookings){
            System.out.println(eachBooking.getCheckInDate() + "  " + bookingRequest.getCheckInDate());
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



//    private boolean roomIsAvailable1(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
//        return existingBookings.stream()
//                .noneMatch(existingBooking ->
//                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
//                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
//                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
//                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
//                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())
//
//                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
//                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())
//
//                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))
//
//                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
//                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))
//
//                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
//                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
//                );
//    }
}
