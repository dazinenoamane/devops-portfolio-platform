package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileRequest(
        @NotBlank @Size(max = 160) String fullName,
        @NotBlank @Size(max = 180) String headline,
        @NotBlank @Size(max = 1200) String bio,
        @NotBlank @Email String email,
        String githubUrl,
        String linkedinUrl
) {
}
