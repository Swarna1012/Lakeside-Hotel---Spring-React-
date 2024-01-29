package com.lakeSide.hotel.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RatingResponses {

    private Long id;
    private BigDecimal rating;
    private String review;
    private String guestName;
}
