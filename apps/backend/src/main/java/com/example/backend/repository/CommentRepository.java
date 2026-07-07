package com.example.backend.repository;

import com.example.backend.entity.CommentStatus;
import com.example.backend.entity.PortfolioComment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<PortfolioComment, Long> {
    List<PortfolioComment> findByStatusOrderByCreatedAtDesc(CommentStatus status);
    List<PortfolioComment> findAllByOrderByCreatedAtDesc();
}
