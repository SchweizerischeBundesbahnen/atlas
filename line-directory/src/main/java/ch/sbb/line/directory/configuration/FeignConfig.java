package ch.sbb.line.directory.configuration;

import ch.sbb.atlas.api.client.AtlasApiFeignClientsConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(AtlasApiFeignClientsConfig.class)
@Configuration
public class FeignConfig {

}
