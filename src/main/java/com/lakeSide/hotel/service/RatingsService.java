package com.lakeSide.hotel.service;

import com.lakeSide.hotel.model.Ratings;

import java.math.BigDecimal;
import java.util.List;

public interface RatingsService {
    Ratings addRating(Long roomId, BigDecimal rating, String review, String guestName);

    Ratings editRating(Long id, BigDecimal rating, String review, String guestName);

    List<Ratings> getAllRatingsByRoomId(Long roomId);

}
