package com.lakeSide.hotel.service;

import com.lakeSide.hotel.model.EmailDetails;

public interface EmailService {

    String sendSimpleEmail(EmailDetails details);
}
