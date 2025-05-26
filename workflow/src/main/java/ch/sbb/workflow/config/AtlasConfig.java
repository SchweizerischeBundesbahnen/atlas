package ch.sbb.workflow.config;

import ch.sbb.atlas.workflow.termination.TerminationStopPointFeatureTogglingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtlasConfig {

  @Bean
  public TerminationStopPointFeatureTogglingService terminationStopPointFeatureTogglingService() {
    return new TerminationStopPointFeatureTogglingService();
  }

}
