package com.example.TickTickServer.controller;

import com.example.TickTickServer.model.User;
import com.example.TickTickServer.service.IUserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            return ResponseEntity.badRequest().body("Missing idToken");
        }

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                return ResponseEntity.status(401).body("Invalid ID token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();

            Optional<User> optionalUser = userService.getUserByEmail(email);
            User user;

            if (optionalUser.isPresent()) {
                user = optionalUser.get();
            } else {
                user = new User();
                user.setEmail(email);
                user.setGoogle(true);
                user.setIs_premium(false);
                userService.save(user);
            }

            Map<String, Object> response = Map.of(
                    "email", user.getEmail(),
                    "isGoogle", user.isGoogle(),
                    "isPremium", user.isIs_premium()
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal server error"
            ));
        }
    }
}
