package com.lakeSide.hotel.controller;

import com.lakeSide.hotel.model.Ratings;
import com.lakeSide.hotel.response.RatingResponses;
import com.lakeSide.hotel.service.RatingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ratings")
public class RatingsController {

    private final RatingsService ratingsService;

    @PostMapping("/add/new-rating/{roomId}")
    public ResponseEntity<Ratings> addRating(
                                    @PathVariable("roomId") Long roomId,
                                    @RequestParam("rating") BigDecimal rating,
                                    @RequestParam("review") String review,
                                    @RequestParam("guestName") String guestName){
        Ratings ratings = ratingsService.addRating(roomId, rating, review, guestName);
        return ResponseEntity.ok(ratings);
    }

    @PutMapping("/edit/rating/{id}")
    public ResponseEntity<Ratings> editRating(
                                    @PathVariable("id") Long id,
                                    @RequestParam("rating") BigDecimal rating,
                                    @RequestParam("review") String review,
                                    @RequestParam("guestName") String guestName){
        Ratings ratings = ratingsService.editRating(id, rating, review, guestName);
        return ResponseEntity.ok(ratings);
    }

    public RatingResponses getRatingResponses(Ratings ratings){
        RatingResponses ratingResponses = new RatingResponses(
                ratings.getId(),
                ratings.getRating(),
                ratings.getReview(),
                ratings.getGuestName()
        );
        return ratingResponses;
    }
}
