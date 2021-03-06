package pl.mesayah.assistance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;

/**
 * Main application class with starting point defined.
 */
@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class
})
public class AssistanceApplication {

    public static final String NAME = "Assistance";


    /**
     * Starting point of this application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {

        // Start Spring Boot application and the contained server.
        SpringApplication.run(AssistanceApplication.class, args);
    }

}

