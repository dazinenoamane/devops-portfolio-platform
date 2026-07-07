package com.example.backend.config;

import com.example.backend.entity.AdminUser;
import com.example.backend.entity.PortfolioProfile;
import com.example.backend.entity.Project;
import com.example.backend.repository.AdminUserRepository;
import com.example.backend.repository.PortfolioProfileRepository;
import com.example.backend.repository.ProjectRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final AdminUserRepository adminUserRepository;
    private final PortfolioProfileRepository profileRepository;
    private final ProjectRepository projectRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    public DataSeeder(AdminUserRepository adminUserRepository, PortfolioProfileRepository profileRepository,
                      ProjectRepository projectRepository, PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.profileRepository = profileRepository;
        this.projectRepository = projectRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedProfile();
        seedProjects();
    }

    private void seedAdmin() {
        if (adminUserRepository.existsByUsername(adminUsername)) {
            return;
        }
        AdminUser admin = new AdminUser();
        admin.setUsername(adminUsername);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole("ROLE_ADMIN");
        adminUserRepository.save(admin);
        log.info("Seeded default admin user '{}'. Change ADMIN_PASSWORD before production.", adminUsername);
    }

    private void seedProfile() {
        if (profileRepository.count() > 0) {
            return;
        }
        PortfolioProfile profile = new PortfolioProfile();
        profile.setFullName("Your Name");
        profile.setHeadline("DevOps Engineering Student");
        profile.setBio("Portfolio cloud-native concu pour pratiquer Docker, Kubernetes, CI/CD, monitoring et securite.");
        profile.setEmail("you@example.com");
        profile.setGithubUrl("https://github.com/your-username");
        profile.setLinkedinUrl("https://linkedin.com/in/your-profile");
        profileRepository.save(profile);
    }

    private void seedProjects() {
        if (projectRepository.count() > 0) {
            return;
        }
        Project project = new Project();
        project.setTitle("DevOps Portfolio Platform");
        project.setDescription("Application Spring Boot, React et PostgreSQL industrialisee avec une chaine DevOps complete.");
        project.setTechnologies(List.of("Spring Boot", "React", "PostgreSQL", "Docker", "Kubernetes"));
        project.setGithubUrl("https://github.com/your-username/devops-portfolio-platform");
        project.setDemoUrl("https://example.com");
        project.setFeatured(true);
        projectRepository.save(project);
    }
}
