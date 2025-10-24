package com.worfwint.tabletoprpgmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Entry point for the Tabletop RPG Manager Spring Boot application.
 * <p>
 * The application enables JPA auditing to automatically populate auditing
 * metadata such as creation and update timestamps.
 */
@SpringBootApplication
@EnableJpaAuditing
public class TabletopRpgManagerApplication {

    /**
     * Boots the Spring application context.
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(TabletopRpgManagerApplication.class, args);
    }

}
