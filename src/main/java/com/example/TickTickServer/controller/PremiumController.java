package com.example.TickTickServer.controller;

import com.example.TickTickServer.model.User;
import com.example.TickTickServer.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/premium")
public class PremiumController {

    private final UserServiceImpl userService;

    @Autowired
    public PremiumController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgradePremium(@RequestBody UserUpgradeRequest request) {
        // Lấy thông tin user từ userId
        Optional<User> userOptional = userService.getUserById(request.getUserId()); // Lấy user theo userId

        if (!userOptional.isPresent()) {
            System.out.println("User not found with id: " + request.getUserId()); // Log khi không tìm thấy user
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOptional.get();
        System.out.println("Found user: " + user.getEmail()); // Log khi tìm thấy user

        // Kiểm tra nếu user đã có Premium
        if (user.isIs_premium()) {
            // Nếu đã premium, trả về thông báo gia hạn thay vì nâng cấp
            System.out.println("User " + user.getEmail() + " already has premium"); // Log nếu đã có premium
            return ResponseEntity.ok("User already has premium");
        }

        // Cập nhật thông tin premium cho user
        user.setIs_premium(true);
        System.out.println("User " + user.getEmail() + " has been upgraded to premium"); // Log khi nâng cấp

        // Lưu lại thay đổi
        try {
            userService.save(user);
            System.out.println("User " + user.getEmail() + " has been successfully saved with premium status"); // Log sau khi lưu
        } catch (Exception e) {
            System.out.println("Error saving user: " + e.getMessage()); // Log lỗi nếu có khi lưu
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving user");
        }

        return ResponseEntity.ok(new PremiumResponse(true));
    }

    // Dùng để phản hồi từ server khi nâng cấp thành công
    public static class PremiumResponse {
        private boolean success;

        public PremiumResponse(boolean success) {
            this.success = success;
        }

        // Getters and setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }

    public static class UserUpgradeRequest {
        private Long userId; // Hoặc String email

        // Getters and setters
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }
    }
}

