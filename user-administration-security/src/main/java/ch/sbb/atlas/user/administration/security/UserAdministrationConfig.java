package ch.sbb.atlas.user.administration.security;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan
@EnableJpaRepositories("ch.sbb")
@EntityScan("ch.sbb")
public class UserAdministrationConfig {

}