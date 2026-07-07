package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ProjectRequest(
        @NotBlank @Size(max = 180) String title,
        @NotBlank @Size(max = 1000) String description,
        List<String> technologies,
        String githubUrl,
        String demoUrl,
        boolean featured
) {
}
