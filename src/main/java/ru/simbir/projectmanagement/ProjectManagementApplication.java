package ru.simbir.projectmanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjectManagementApplication {

    private static final Logger LOGGER = LogManager.getLogger(ProjectManagementApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ProjectManagementApplication.class, args);
        LOGGER.info("STAAAAAAAAAART!");
    }

}
