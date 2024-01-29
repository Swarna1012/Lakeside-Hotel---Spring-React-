package com.lakeSide.hotel.service;

import com.lakeSide.hotel.model.User;

import java.util.List;

public interface UserService {

    User registerUser(User user);
    List<User> getUsers();
    void deleteUser(String email);
    User getUser(String email);

}
