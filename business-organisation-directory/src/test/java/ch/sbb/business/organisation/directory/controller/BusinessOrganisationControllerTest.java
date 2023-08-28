package ch.sbb.business.organisation.directory.controller;

import ch.sbb.business.organisation.directory.service.BusinessOrganisationAmazonService;
import ch.sbb.business.organisation.directory.service.BusinessOrganisationService;
import ch.sbb.business.organisation.directory.service.export.BusinessOrganisationVersionExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class BusinessOrganisationControllerTest {

  @Mock
  private BusinessOrganisationService service;
  @Mock
  private BusinessOrganisationVersionExportService exportService;

  @Mock
  private BusinessOrganisationAmazonService businessOrganisationAmazonService;

  private BusinessOrganisationController businessOrganisationController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    businessOrganisationController = new BusinessOrganisationController(service, exportService, businessOrganisationAmazonService);
  }

  @Test
  void shouldSyncWithService() {
    businessOrganisationController.syncBusinessOrganisations();
    verify(service).syncAllBusinessOrganisations();
  }

}