package com.example.backend.dto;

public record ProfileResponse(Long id, String fullName, String headline, String bio, String email, String githubUrl, String linkedinUrl) {
}
