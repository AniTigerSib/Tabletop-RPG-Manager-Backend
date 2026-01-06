package com.worfwint.tabletoprpgmanager.common.config;

import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.worfwint.tabletoprpgmanager.user.entity.User;
import com.worfwint.tabletoprpgmanager.user.entity.UserRole;
import com.worfwint.tabletoprpgmanager.user.repository.UserRepository;

/**
 * Creates a bootstrap administrator account when the database is empty so the
 * system is manageable without manual SQL edits.
 */
@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.username:admin}")
    private String adminUsername;

    @Value("${app.bootstrap.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.password:change-me}")
    private String adminPassword;

    public AdminBootstrap(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            return;
        }

        if (adminPassword == null || adminPassword.isBlank()) {
            LOG.warn("Skipping admin bootstrap because no password was provided");
            return;
        }

        User admin = new User(adminUsername, adminEmail, passwordEncoder.encode(adminPassword));
        admin.setDisplayName("Administrator");
        admin.setRoles(EnumSet.of(UserRole.ADMIN, UserRole.USER));

        userRepository.save(admin);

        LOG.info("Bootstrap admin account '{}' created. Please change the password immediately.", adminUsername);
    }
}
