package com.lakeSide.hotel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailDetails {

    private String recipient;
    private String subject;
    private String msgBody;
}
