package ch.sbb.mail.config;

import ch.sbb.atlas.base.service.model.configuration.CorrelationIdFilterConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(CorrelationIdFilterConfig.class)
public class CorrelationIdConfig {

}
