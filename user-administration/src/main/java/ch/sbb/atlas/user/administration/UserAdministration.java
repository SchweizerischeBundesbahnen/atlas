package ch.sbb.atlas.user.administration;

import ch.sbb.atlas.base.service.model.configuration.AtlasExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UserAdministration {

    public static void main(String[] args) {
        SpringApplication.run(UserAdministration.class, args);
    }

    @Bean
    public AtlasExceptionHandler atlasExceptionHandler() {
        return new AtlasExceptionHandler();
    }
}
