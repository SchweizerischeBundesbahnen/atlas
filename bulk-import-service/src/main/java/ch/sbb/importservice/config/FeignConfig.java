package ch.sbb.importservice.config;

import ch.sbb.atlas.api.client.AtlasApiFeignClientsConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(AtlasApiFeignClientsConfig.class)
@Configuration
public class FeignConfig {

}
