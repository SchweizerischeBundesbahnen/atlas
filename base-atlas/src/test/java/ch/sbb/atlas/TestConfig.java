package ch.sbb.atlas;

import ch.sbb.atlas.business.organisation.repository.BusinessOrganisationVersionSharingDataAccessor;
import ch.sbb.atlas.transport.company.repository.TransportCompanySharingDataAccessor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

  @MockBean
  BusinessOrganisationVersionSharingDataAccessor businessOrganisationVersionSharingDataAccessor;

  @MockBean
  TransportCompanySharingDataAccessor transportCompanySharingDataAccessor;
}
