package com.example.TickTickServer.controller;

import com.example.TickTickServer.model.User;
import com.example.TickTickServer.service.IUserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String CLIENT_ID = "814608883132-3rjf27ov6f0277opa59d1nuugvque1ov.apps.googleusercontent.com";

    @Autowired
    private IUserService userService;

    @PostMapping("/google")
    public ResponseEntity<?> verifyGoogleToken(@RequestBody Map<String, String> body) {
        String idTokenString = body.get("idToken");
        if (idTokenString == null) {
            System.out.println("❌ Missing idToken in request body");
            return ResponseEntity.badRequest().body("Missing idToken");
        }

        try {
            System.out.println("📥 Received idToken: " + idTokenString);

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                System.out.println("❌ Token verification failed (idToken == null)");
                return ResponseEntity.status(401).body("Invalid ID token");
            }

            // Lấy thông tin từ token
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();

            System.out.println("✅ Token verified. Email: " + email);

            // Kiểm tra xem user đã tồn tại trong DB chưa
            Optional<User> optionalUser = userService.getUserByEmail(email);
            User user;

            if (optionalUser.isPresent()) {
                user = optionalUser.get();
                System.out.println("ℹ️ User already exists in DB: " + user.getEmail());
            } else {
                // Nếu chưa có user, thêm mới
                user = new User();
                user.setEmail(email);
                user.setGoogle(true);
                user.setIs_premium(false); // ví dụ hardcode
                userService.save(user);
                System.out.println("➕ New user created and saved: " + user.getEmail());
            }

            // Trả thông tin người dùng về cho client
            Map<String, Object> response = Map.of(
                    "email", user.getEmail(),
                    "isGoogle", user.isGoogle(),
                    "isPremium", user.isIs_premium()
            );
            System.out.println("✅ Returning user info: " + response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();

            System.out.println("🔥 Exception during token verification or DB access:");
            System.out.println(stackTrace);

            // Trả chi tiết lỗi (chỉ nên dùng trong môi trường phát triển)
            Map<String, Object> errorResponse = Map.of(
                    "error", "Internal error during token verification",
                    "exception", e.getClass().getName(),
                    "message", e.getMessage(),
                    "stackTrace", stackTrace
            );

            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
