package nl.vodafoneziggo.ccam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main class for this application.
 */
@SpringBootApplication
@SuppressWarnings({"checkstyle:HideUtilityClassConstructor", "PMD.UseUtilityClass"})
public class Application {

    /**
     * starts the spring boot application.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
