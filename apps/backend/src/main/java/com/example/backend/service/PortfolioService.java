package com.example.backend.service;

import com.example.backend.dto.CommentRequest;
import com.example.backend.dto.CommentResponse;
import com.example.backend.dto.PortfolioResponse;
import com.example.backend.dto.ProfileRequest;
import com.example.backend.dto.ProfileResponse;
import com.example.backend.dto.ProjectRequest;
import com.example.backend.dto.ProjectResponse;
import com.example.backend.entity.CommentStatus;
import com.example.backend.entity.PortfolioComment;
import com.example.backend.entity.PortfolioProfile;
import com.example.backend.entity.Project;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.PortfolioProfileRepository;
import com.example.backend.repository.ProjectRepository;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PortfolioService {
    private static final Logger log = LoggerFactory.getLogger(PortfolioService.class);

    private final PortfolioProfileRepository profileRepository;
    private final ProjectRepository projectRepository;
    private final CommentRepository commentRepository;

    public PortfolioService(PortfolioProfileRepository profileRepository, ProjectRepository projectRepository,
                            CommentRepository commentRepository) {
        this.profileRepository = profileRepository;
        this.projectRepository = projectRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional(readOnly = true)
    public PortfolioResponse getPortfolio() {
        return new PortfolioResponse(getProfile(), getProjects(), getApprovedComments());
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile() {
        PortfolioProfile profile = profileRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio profile not found"));
        return toProfileResponse(profile);
    }

    @Transactional
    public ProfileResponse updateProfile(ProfileRequest request) {
        PortfolioProfile profile = profileRepository.findAll().stream().findFirst().orElseGet(PortfolioProfile::new);
        profile.setFullName(request.fullName());
        profile.setHeadline(request.headline());
        profile.setBio(request.bio());
        profile.setEmail(request.email());
        profile.setGithubUrl(request.githubUrl());
        profile.setLinkedinUrl(request.linkedinUrl());
        return toProfileResponse(profileRepository.save(profile));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjects() {
        return projectRepository.findAllByOrderByFeaturedDescCreatedAtDesc().stream().map(this::toProjectResponse).toList();
    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        Project project = new Project();
        applyProjectRequest(project, request);
        return toProjectResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        applyProjectRequest(project, request);
        return toProjectResponse(projectRepository.save(project));
    }

    @Transactional
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found");
        }
        projectRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getApprovedComments() {
        return commentRepository.findByStatusOrderByCreatedAtDesc(CommentStatus.APPROVED).stream().map(this::toCommentResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getAllComments() {
        return commentRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toCommentResponse).toList();
    }

    @Transactional
    public CommentResponse submitComment(CommentRequest request) {
        PortfolioComment comment = new PortfolioComment();
        comment.setAuthorName(request.authorName());
        comment.setAuthorEmail(request.authorEmail());
        comment.setContent(request.content());
        comment.setStatus(CommentStatus.PENDING);
        PortfolioComment saved = commentRepository.save(comment);
        log.info("New comment submitted by '{}' and waiting for moderation", request.authorName());
        return toCommentResponse(saved);
    }

    @Transactional
    public CommentResponse moderateComment(Long id, CommentStatus status) {
        PortfolioComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        comment.setStatus(status);
        return toCommentResponse(commentRepository.save(comment));
    }

    private void applyProjectRequest(Project project, ProjectRequest request) {
        project.setTitle(request.title());
        project.setDescription(request.description());
        project.setTechnologies(request.technologies() == null ? new ArrayList<>() : request.technologies());
        project.setGithubUrl(request.githubUrl());
        project.setDemoUrl(request.demoUrl());
        project.setFeatured(request.featured());
    }

    private ProfileResponse toProfileResponse(PortfolioProfile profile) {
        return new ProfileResponse(profile.getId(), profile.getFullName(), profile.getHeadline(), profile.getBio(),
                profile.getEmail(), profile.getGithubUrl(), profile.getLinkedinUrl());
    }

    private ProjectResponse toProjectResponse(Project project) {
        return new ProjectResponse(project.getId(), project.getTitle(), project.getDescription(), new ArrayList<>(project.getTechnologies()),
                project.getGithubUrl(), project.getDemoUrl(), project.isFeatured(), project.getCreatedAt());
    }

    private CommentResponse toCommentResponse(PortfolioComment comment) {
        return new CommentResponse(comment.getId(), comment.getAuthorName(), comment.getContent(), comment.getStatus(), comment.getCreatedAt());
    }
}
