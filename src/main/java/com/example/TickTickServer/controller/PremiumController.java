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
        // Lấy user theo email (do app gửi email)
        Optional<User> userOptional = userService.getUserByEmail(request.getEmail());

        if (!userOptional.isPresent()) {
            System.out.println("User not found with email: " + request.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOptional.get();
        System.out.println("Found user: " + user.getEmail());

        // Nếu đã premium thì trả về expirationDate hiện tại
        if (user.isIs_premium()) {
            System.out.println("User " + user.getEmail() + " already has premium");
            return ResponseEntity.ok(new PremiumResponse(true, user.getPremium_expiration_date()));
        }

        user.setIs_premium(true);
        String expirationDate = "2099-12-31"; // Hoặc bất kỳ ngày hết hạn "vĩnh viễn"
        user.setPremium_expiration_date(expirationDate);

        try {
            userService.save(user);
            System.out.println("User " + user.getEmail() + " upgraded to premium");
        } catch (Exception e) {
            System.out.println("Error saving user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving user");
        }

        return ResponseEntity.ok(new PremiumResponse(true, expirationDate));
    }

    // Phản hồi gồm success và expirationDate
    public static class PremiumResponse {
        private boolean success;
        private String expirationDate;

        public PremiumResponse(boolean success, String expirationDate) {
            this.success = success;
            this.expirationDate = expirationDate;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getExpirationDate() {
            return expirationDate;
        }

        public void setExpirationDate(String expirationDate) {
            this.expirationDate = expirationDate;
        }
    }

    // DTO request: Gửi email từ app
    public static class UserUpgradeRequest {
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
