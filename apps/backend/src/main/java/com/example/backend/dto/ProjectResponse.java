package com.example.backend.dto;

import java.time.Instant;
import java.util.List;

public record ProjectResponse(Long id, String title, String description, List<String> technologies, String githubUrl,
                              String demoUrl, boolean featured, Instant createdAt) {
}
