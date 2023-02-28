package ch.sbb.atlas.timetable.hearing.config;

import ch.sbb.atlas.api.client.AtlasApiFeignClientsConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(AtlasApiFeignClientsConfig.class)
@Configuration
public class FeignConfig {

}
