package com.lakeSide.hotel.service.impl;

import com.lakeSide.hotel.model.Ratings;
import com.lakeSide.hotel.model.Room;
import com.lakeSide.hotel.repository.RatingsRepository;
import com.lakeSide.hotel.repository.RoomRepository;
import com.lakeSide.hotel.service.RatingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingsServiceIMPL implements RatingsService {

    private final RatingsRepository ratingsRepository;
    private final RoomRepository roomRepository;

    @Override
    public Ratings addRating(Long roomId, BigDecimal rating, String review, String guestName) {

        Ratings ratings = new Ratings();
        ratings.setRating(rating);
        ratings.setReview(review);
        ratings.setGuestName(guestName);

        Room room = new Room();
        room.setId(roomId);
        ratings.setRoom(room);
        ratingsRepository.save(ratings);

        Room room1 = roomRepository.findById(roomId).get();
        BigDecimal avgRating = ratingsRepository.averageRatingsByRoomId(roomId);
        room1.setStars(avgRating);
        roomRepository.save(room1);
        return ratings;
    }

    @Override
    public Ratings editRating(Long id, BigDecimal rating, String review, String guestName) {
        Ratings ratings = ratingsRepository.findById(id).get();
        ratings.setRating(rating);
        ratings.setReview(review);
        ratings.setGuestName(guestName);
        ratingsRepository.save(ratings);

        Room room = ratings.getRoom();
        Room room1 = roomRepository.findById(room.getId()).get();
        BigDecimal avgRating = ratingsRepository.averageRatingsByRoomId(room1.getId());
        room1.setStars(avgRating);
        roomRepository.save(room1);

        return ratingsRepository.save(ratings);
    }


    public List<Ratings> getAllRatingsByRoomId(Long roomId){
        return ratingsRepository.findAllByRoomId(roomId);
    }
}
