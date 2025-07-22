package ch.sbb.prm.directory.configuration;

import ch.sbb.atlas.kafka.SharedKafkaConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import(SharedKafkaConfig.class)
@Profile("!integration-test")
public class KafkaConfig {

}
