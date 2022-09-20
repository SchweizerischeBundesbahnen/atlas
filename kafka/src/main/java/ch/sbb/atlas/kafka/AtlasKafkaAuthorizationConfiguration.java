package ch.sbb.atlas.kafka;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:kafka-authorization-application.yml", factory = YamlPropertySourceFactory.class)
public class AtlasKafkaAuthorizationConfiguration {

}
