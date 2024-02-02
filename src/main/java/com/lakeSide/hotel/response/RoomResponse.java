package com.lakeSide.hotel.response;

import com.lakeSide.hotel.model.Ratings;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@Data
public class RoomResponse {

    private Long id;
    private String roomType;
    private BigDecimal roomPrice;
    private boolean isBooked = false;
    private String photo;
    private BigDecimal stars;
    private List<BookingResponse> bookings;
    private List<RatingResponses> ratings;

    public RoomResponse(Long id, String roomType, BigDecimal roomPrice) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
    }

    public RoomResponse(Long id, String roomType, BigDecimal roomPrice, byte[] photoBytes) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.photo = photoBytes != null ? Base64.encodeBase64String(photoBytes) : null;

    }

    public RoomResponse(Long id, String roomType, BigDecimal roomPrice, boolean isBooked, byte[] photoBytes, List<BookingResponse> bookings, List<RatingResponses> ratings) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.isBooked = isBooked;
        this.photo = photoBytes != null ? Base64.encodeBase64String(photoBytes) : null;
        this.bookings = bookings;
        this.ratings = ratings;
    }

    public RoomResponse(Long id, String roomType, BigDecimal roomPrice, BigDecimal stars, byte[] photoBytes, List<RatingResponses> ratings) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.stars = stars;
        this.photo = photoBytes != null ? Base64.encodeBase64String(photoBytes) : null;
        this.ratings = ratings;
    }
}
