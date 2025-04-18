package com.example.TickTickServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.*;
import java.util.Base64;

@SpringBootApplication
public class TickTickServerApplication {

	public static void main(String[] args) {
		// ✅ Decode base64 keystore before starting Spring Boot
		initKeystoreFromEnv();

		SpringApplication.run(TickTickServerApplication.class, args);
	}

	private static void initKeystoreFromEnv() {
		try {
			String base64 = System.getenv("KEYSTORE_B64");
			if (base64 == null || base64.isEmpty()) {
				System.err.println("⚠️  KEYSTORE_B64 environment variable is not set.");
				return;
			}

			byte[] decoded = Base64.getDecoder().decode(base64);
			Path path = Paths.get("/tmp/keystore.p12");

			Files.write(path, decoded);
			System.out.println("✅ Keystore written to /tmp/keystore.p12");
		} catch (Exception e) {
			System.err.println("❌ Failed to initialize keystore from base64: " + e.getMessage());
			e.printStackTrace();
			System.exit(1); // Ngăn app khởi động nếu lỗi
		}
	}
}
