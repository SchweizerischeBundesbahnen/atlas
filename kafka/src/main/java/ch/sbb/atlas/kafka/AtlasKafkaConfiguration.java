package ch.sbb.atlas.kafka;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:kafka-application.yml", factory = YamlPropertySourceFactory.class)
@Import(AtlasKafkaAuthorizationConfiguration.class)
public class AtlasKafkaConfiguration {

}
