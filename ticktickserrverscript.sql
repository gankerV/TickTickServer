-- Xoá database nếu đã tồn tại
DROP DATABASE IF EXISTS TickTickServer;

-- Tạo database mới
CREATE DATABASE TickTickServer;

-- Sử dụng database
USE TickTickServer;

-- Xoá bảng nếu đã tồn tại
DROP TABLE IF EXISTS tbl_user;

-- Tạo bảng người dùng
CREATE TABLE tbl_user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    isGoogle TINYINT(1) DEFAULT 0,
    is_premium TINYINT(1) DEFAULT 0
);
