package com.example.backend.controller;

import com.example.backend.dto.CommentRequest;
import com.example.backend.dto.CommentResponse;
import com.example.backend.dto.PortfolioResponse;
import com.example.backend.dto.ProjectResponse;
import com.example.backend.service.PortfolioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PublicPortfolioController {
    private final PortfolioService portfolioService;

    public PublicPortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/portfolio")
    public PortfolioResponse portfolio() {
        return portfolioService.getPortfolio();
    }

    @GetMapping("/projects")
    public List<ProjectResponse> projects() {
        return portfolioService.getProjects();
    }

    @GetMapping("/comments")
    public List<CommentResponse> comments() {
        return portfolioService.getApprovedComments();
    }

    @PostMapping("/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse submitComment(@Valid @RequestBody CommentRequest request) {
        return portfolioService.submitComment(request);
    }
}
