package com.example.TickTickServer.service;

import com.example.TickTickServer.model.User;
import java.util.Optional;

public interface IUserService {
    Optional<User> getUserByEmail(String email);

    Optional<User> getUserById(Long id);  // Thêm phương thức lấy user theo ID

    User save(User user);
}