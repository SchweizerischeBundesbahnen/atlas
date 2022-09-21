package ch.sbb.atlas.user.administration.config;

import ch.sbb.atlas.kafka.AtlasKafkaConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(AtlasKafkaConfiguration.class)
public class KafkaConfig {

}