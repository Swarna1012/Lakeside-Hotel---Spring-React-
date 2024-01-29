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

        System.out.println("add new ratings ");
        Ratings ratings = new Ratings();
        ratings.setRating(rating);
        ratings.setReview(review);
        ratings.setGuestName(guestName);

        Room room = new Room();
        room.setId(roomId);
        ratings.setRoom(room);
        Ratings ratings1 = ratingsRepository.save(ratings);
        System.out.println("ratings " + ratings);

        Room room1 = roomRepository.findById(roomId).get();
        BigDecimal avgRating = ratingsRepository.averageRatingsByRoomId(roomId);

        System.out.println("average rating " + avgRating);
        room1.setStars(avgRating);

        roomRepository.save(room1);
        return ratings;
    }

//    @Override
//    public Ratings editRating(Long id, Integer rating, String review) {
//        Ratings ratings = ratingsRepository.findById(id).get();
//        ratings.setRating(rating);
//        ratings.setReview(review);
//
//        return ratingsRepository.save(ratings);
//    }
//

    public List<Ratings> getAllRatingsByRoomId(Long roomId){
        return ratingsRepository.findAllByRoomId(roomId);
    }
}
