package com.lakeSide.hotel.service;

import com.lakeSide.hotel.model.Room;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RoomService {
    Room addNewRoom(MultipartFile file, String roomType, BigDecimal roomPrice) throws IOException, SQLException;

    List<String> getRoomTypes();

    List<Room> getAllRooms();

    ResponseEntity<Void> deleteRoom(Long roomId);

    Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes) throws SQLException;

    Optional<Room> getRoomById(Long roomId);

    byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException;

//    List<String> getAvailable();

    List<Room> getFilteredRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType, BigDecimal minPrice, BigDecimal maxPrice);

    Map getAvailableDate(Long roomId, LocalDate startDate, LocalDate endDate);
}
