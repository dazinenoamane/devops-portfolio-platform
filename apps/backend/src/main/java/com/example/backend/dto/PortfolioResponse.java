package com.example.backend.dto;

import java.util.List;

public record PortfolioResponse(ProfileResponse profile, List<ProjectResponse> projects, List<CommentResponse> comments) {
}
