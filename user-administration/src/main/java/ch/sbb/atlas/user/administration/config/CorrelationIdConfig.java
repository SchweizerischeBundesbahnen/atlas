package ch.sbb.atlas.user.administration.config;

import ch.sbb.atlas.base.service.model.configuration.CorrelationIdFilterConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CorrelationIdFilterConfig.class)
public class CorrelationIdConfig {

}
