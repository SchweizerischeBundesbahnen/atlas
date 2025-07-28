package ch.sbb.business.organisation.directory.controller;

import static org.mockito.Mockito.verify;

import ch.sbb.business.organisation.directory.service.BusinessOrganisationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BusinessOrganisationControllerInternalTest {

  @Mock
  private BusinessOrganisationService service;

  private BusinessOrganisationControllerInternal businessOrganisationControllerInternal;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    businessOrganisationControllerInternal = new BusinessOrganisationControllerInternal(service);
  }

  @Test
  void shouldSyncWithService() {
    businessOrganisationControllerInternal.syncBusinessOrganisations();
    verify(service).syncAllBusinessOrganisations();
  }

}
