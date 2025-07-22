package ch.sbb.prm.directory.configuration;

import ch.sbb.atlas.kafka.SharedKafkaConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SharedKafkaConfig.class)
public class KafkaConfig {

}
