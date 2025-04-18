package com.example.TickTickServer.service;

import com.example.TickTickServer.model.User;
import java.util.Optional;

public interface IUserService {
    Optional<User> getUserByEmail(String email);

    User save(User user);
}
