package ch.sbb.mail.config;

import ch.sbb.atlas.configuration.filter.CorrelationIdFilterConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CorrelationIdFilterConfig.class)
public class CorrelationIdConfig {

}
