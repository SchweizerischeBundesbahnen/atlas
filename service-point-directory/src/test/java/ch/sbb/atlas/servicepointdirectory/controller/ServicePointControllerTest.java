package ch.sbb.atlas.servicepointdirectory.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.AbbreviationUpdateNotAllowedException;
import ch.sbb.atlas.servicepointdirectory.exception.InvalidAbbreviationException;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberAlreadyExistsException;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointVersionMapper;
import ch.sbb.atlas.servicepointdirectory.service.ServicePointDistributor;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointFotCommentService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointImportService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

  private ServicePointController servicePointController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    servicePointController = new ServicePointController(servicePointService, servicePointFotCommentService,
        servicePointImportService, geoReferenceService, servicePointDistributor);

    when(servicePointService.save(any())).then(i -> i.getArgument(0, ServicePointVersion.class));
  }

  @Test
  void shouldReturnConflictExceptionWhenNumberAlreadyUsed() {
    when(servicePointService.isServicePointNumberExisting(any())).thenReturn(true);
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberWithoutCheckDigit(8507000)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    assertThrows(ServicePointNumberAlreadyExistsException.class,
        () -> servicePointController.createServicePoint(servicePointVersionModel));
  }

  @Test
  public void testWhenNewAbbreviationIsBlank() {
    CreateServicePointVersionModel createServicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberWithoutCheckDigit(8507111)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:123456")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    ServicePointVersion servicePointVersion = ServicePointVersionMapper.toEntity(createServicePointVersionModel);

    assertDoesNotThrow(() -> servicePointController.validateAndSetAbbreviation(servicePointVersion, createServicePointVersionModel.getAbbreviation(), "existing"));
  }
  @Test
  void testWhenBusinessOrganisationNotInList(){
    CreateServicePointVersionModel createServicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberWithoutCheckDigit(8507111)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:123456")
        .abbreviation("TEST")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    ServicePointVersion servicePointVersion = ServicePointVersionMapper.toEntity(createServicePointVersionModel);


    assertThrows(AbbreviationUpdateNotAllowedException.class,
        () -> servicePointController.validateAndSetAbbreviation(servicePointVersion, createServicePointVersionModel.getAbbreviation(), "TEST"));
  }

  @Test
  public void testWhenExistingAbbreviationDoesNotMatchNewAbbreviation() {
    CreateServicePointVersionModel createServicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberWithoutCheckDigit(8507111)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:100016")
        .abbreviation("TEST")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    ServicePointVersion servicePointVersion = ServicePointVersionMapper.toEntity(createServicePointVersionModel);

    assertThrows(AbbreviationUpdateNotAllowedException.class,
        () -> servicePointController.validateAndSetAbbreviation(servicePointVersion, createServicePointVersionModel.getAbbreviation(), "BUCH"));
  }

  @Test
  public void testWhenAbbreviationIsNotUnique() {
    CreateServicePointVersionModel createServicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberWithoutCheckDigit(8507111)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:100016")
        .abbreviation("TEST")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    ServicePointVersion servicePointVersion = ServicePointVersionMapper.toEntity(createServicePointVersionModel);

    when(servicePointService.isAbbrevitionUnique(servicePointVersion.getAbbreviation(), servicePointVersion.getNumber())).thenReturn(false);
    assertThrows(InvalidAbbreviationException.class,
        () -> servicePointController.validateAndSetAbbreviation(servicePointVersion, createServicePointVersionModel.getAbbreviation(), null));
  }

  @Test
  public void testSuccessfullAbbreviationUpdate() {
    CreateServicePointVersionModel createServicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberWithoutCheckDigit(8507111)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:100016")
        .abbreviation("TEST")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    ServicePointVersion servicePointVersion = ServicePointVersionMapper.toEntity(createServicePointVersionModel);

    when(servicePointService.isAbbrevitionUnique(createServicePointVersionModel.getAbbreviation(), servicePointVersion.getNumber())).thenReturn(true);
    assertDoesNotThrow(() -> servicePointController.validateAndSetAbbreviation(servicePointVersion, createServicePointVersionModel.getAbbreviation(), "TEST"));
    assertEquals(servicePointVersion.getAbbreviation(), servicePointVersion.getAbbreviation());
  }
}