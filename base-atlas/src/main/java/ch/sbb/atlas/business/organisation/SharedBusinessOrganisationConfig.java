package ch.sbb.atlas.business.organisation;

import ch.sbb.atlas.business.organisation.repository.BusinessOrganisationVersionSharingDataAccessor;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationConsumer;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedBusinessOrganisationConfig {

  private final BusinessOrganisationVersionSharingDataAccessor businessOrganisationVersionSharingDataAccessor;

  @Autowired
  public SharedBusinessOrganisationConfig(
      BusinessOrganisationVersionSharingDataAccessor businessOrganisationVersionSharingDataAccessor) {
    this.businessOrganisationVersionSharingDataAccessor = businessOrganisationVersionSharingDataAccessor;
  }

  @Bean
  public SharedBusinessOrganisationConsumer sharedBusinessOrganisationConsumer() {
    return new SharedBusinessOrganisationConsumer(businessOrganisationVersionSharingDataAccessor);
  }

  @Bean
  public SharedBusinessOrganisationService sharedBusinessOrganisationService() {
    return new SharedBusinessOrganisationService(businessOrganisationVersionSharingDataAccessor);
  }

}
