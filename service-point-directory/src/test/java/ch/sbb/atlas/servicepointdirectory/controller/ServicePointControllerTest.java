package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberAlreadyExistsException;
import ch.sbb.atlas.servicepointdirectory.service.ServicePointDistributor;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointFotCommentService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointImportService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointNumberService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ServicePointControllerTest {

  @Mock
  private ServicePointService servicePointService;
  @Mock
  private ServicePointFotCommentService servicePointFotCommentService;
  @Mock
  private ServicePointImportService servicePointImportService;
  @Mock
  private GeoReferenceService geoReferenceService;
  @Mock
  private ServicePointDistributor servicePointDistributor;
  @Mock
  private ServicePointNumberService servicePointNumberService;

  private ServicePointController servicePointController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    servicePointController = new ServicePointController(servicePointService, servicePointFotCommentService,
        servicePointImportService, geoReferenceService, servicePointDistributor, servicePointNumberService);

    when(servicePointService.create(any(), any(), any())).then(i -> i.getArgument(0, ServicePointVersion.class));
  }

  @Test
  void shouldReturnConflictExceptionWhenNumberAlreadyUsed() {
    when(servicePointService.isServicePointNumberExisting(any())).thenReturn(true);
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberShort(7000)
        .country(Country.JAPAN)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    assertThrows(ServicePointNumberAlreadyExistsException.class,
        () -> servicePointController.createServicePoint(servicePointVersionModel));
  }
}
