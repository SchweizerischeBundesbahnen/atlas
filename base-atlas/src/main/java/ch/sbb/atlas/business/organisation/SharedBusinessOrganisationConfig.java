package ch.sbb.atlas.business.organisation;

import ch.sbb.atlas.business.organisation.repository.SharedBusinessOrganisationVersionRepository;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class SharedBusinessOrganisationConfig {

  @Bean
  public SharedBusinessOrganisationConsumer sharedBusinessOrganisationConsumer(
      SharedBusinessOrganisationVersionRepository sharedBusinessOrganisationVersionRepository) {
    return new SharedBusinessOrganisationConsumer(sharedBusinessOrganisationVersionRepository);
  }

}
