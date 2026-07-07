package com.example.backend.dto;

import com.example.backend.entity.CommentStatus;
import java.time.Instant;

public record CommentResponse(Long id, String authorName, String content, CommentStatus status, Instant createdAt) {
}
