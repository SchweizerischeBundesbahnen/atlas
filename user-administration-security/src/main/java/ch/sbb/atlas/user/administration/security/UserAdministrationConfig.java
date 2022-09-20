package ch.sbb.atlas.user.administration.security;

import ch.sbb.atlas.kafka.AtlasKafkaConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import(AtlasKafkaConfiguration.class)
public class UserAdministrationConfig {

}
