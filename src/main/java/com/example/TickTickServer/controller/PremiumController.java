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
        // Lấy thông tin user từ email
        Optional<User> userOptional = userService.getUserByEmail(request.getEmail());

        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOptional.get();

        // Kiểm tra nếu user đã có Premium
        if (user.isIs_premium()) {
            // Nếu đã premium, trả về thông báo gia hạn thay vì nâng cấp
            return ResponseEntity.ok("User already has premium");
        }

        // Cập nhật thông tin premium cho user
        user.setIs_premium(true);

        // Lưu lại thay đổi
        userService.save(user);

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

    // DTO request body (UserUpgradeRequest) để lấy email từ client
    public static class UserUpgradeRequest {
        private String email;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
