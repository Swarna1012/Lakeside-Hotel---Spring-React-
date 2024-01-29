package com.lakeSide.hotel.repository;

import com.lakeSide.hotel.model.Ratings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RatingsRepository extends JpaRepository<Ratings, Long> {
    List<Ratings> findAllByRoomId(Long roomId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Ratings r WHERE r.room.id = :roomId")
    BigDecimal averageRatingsByRoomId(@Param("roomId") Long roomId);
}
