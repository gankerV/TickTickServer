package com.example.TickTickServer.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private boolean isGoogle;

    private boolean is_premium;

    @Column(name = "premium_expiration_date")
    private String premium_expiration_date; // Dùng String để khớp với định dạng SQLite

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isGoogle() {
        return isGoogle;
    }

    public void setGoogle(boolean google) {
        isGoogle = google;
    }

    public boolean isIs_premium() {
        return is_premium;
    }

    public void setIs_premium(boolean is_premium) {
        this.is_premium = is_premium;
    }

    public String getPremium_expiration_date() {
        return premium_expiration_date;
    }

    public void setPremium_expiration_date(String premium_expiration_date) {
        this.premium_expiration_date = premium_expiration_date;
    }
}
