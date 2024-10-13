package com.security_board.security.jwt.dto;


public record TokenRequest(String email, String password) {
}