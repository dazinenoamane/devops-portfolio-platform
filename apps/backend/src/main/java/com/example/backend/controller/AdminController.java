package com.example.backend.controller;

import com.example.backend.dto.CommentResponse;
import com.example.backend.dto.ProfileRequest;
import com.example.backend.dto.ProfileResponse;
import com.example.backend.dto.ProjectRequest;
import com.example.backend.dto.ProjectResponse;
import com.example.backend.entity.CommentStatus;
import com.example.backend.service.PortfolioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final PortfolioService portfolioService;

    public AdminController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/profile")
    public ProfileResponse profile() {
        return portfolioService.getProfile();
    }

    @PutMapping("/profile")
    public ProfileResponse updateProfile(@Valid @RequestBody ProfileRequest request) {
        return portfolioService.updateProfile(request);
    }

    @PostMapping("/projects")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(@Valid @RequestBody ProjectRequest request) {
        return portfolioService.createProject(request);
    }

    @PutMapping("/projects/{id}")
    public ProjectResponse updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return portfolioService.updateProject(id, request);
    }

    @DeleteMapping("/projects/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long id) {
        portfolioService.deleteProject(id);
    }

    @GetMapping("/comments")
    public List<CommentResponse> comments() {
        return portfolioService.getAllComments();
    }

    @PatchMapping("/comments/{id}/approve")
    public CommentResponse approveComment(@PathVariable Long id) {
        return portfolioService.moderateComment(id, CommentStatus.APPROVED);
    }

    @PatchMapping("/comments/{id}/reject")
    public CommentResponse rejectComment(@PathVariable Long id) {
        return portfolioService.moderateComment(id, CommentStatus.REJECTED);
    }
}
