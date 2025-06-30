package ch.sbb.atlas;

import ch.sbb.atlas.business.organisation.repository.BusinessOrganisationVersionSharingDataAccessor;
import ch.sbb.atlas.transport.company.repository.TransportCompanySharingDataAccessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Configuration
public class TestConfig {

  @MockitoBean
  BusinessOrganisationVersionSharingDataAccessor businessOrganisationVersionSharingDataAccessor;

  @MockitoBean
  TransportCompanySharingDataAccessor transportCompanySharingDataAccessor;
}
