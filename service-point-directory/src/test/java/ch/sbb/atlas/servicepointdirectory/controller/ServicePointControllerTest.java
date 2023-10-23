package ch.sbb.atlas.servicepointdirectory.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
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

    assertDoesNotThrow(() -> servicePointController.validateAndSetAbbreviationForCreate(servicePointVersion, createServicePointVersionModel.getAbbreviation()));
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
        () -> servicePointController.commonAbbreviationValidations(servicePointVersion, createServicePointVersionModel.getAbbreviation()));
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
        () -> servicePointController.commonAbbreviationValidations(servicePointVersion, createServicePointVersionModel.getAbbreviation()));
  }

  @Test
  public void testWhenServicePointHasAbbreviation() {
    ServicePointVersion existingServicePointVersion = ServicePointVersion.builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(2031231))
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:100016")
        .abbreviation("TEST")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    ServicePointVersion servicePointVersion = ServicePointVersion.builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(2031231))
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:100016")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    when(servicePointService.hasServicePointVersionAbbreviation(existingServicePointVersion, "ABCD")).thenReturn(true);
    assertThrows(AbbreviationUpdateNotAllowedException.class,
        () -> servicePointController.validateAndSetAbbreviationForUpdate(existingServicePointVersion, servicePointVersion, "ABCD"));
  }

  @Test
  public void testIsServicePointVersionHighDate() {
    ServicePointVersion existingServicePointVersion = ServicePointVersion.builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(2031231))
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:100016")
        .abbreviation("TEST")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    ServicePointVersion newServicePointVersion = ServicePointVersion.builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(2031231))
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:100016")
        .abbreviation("TEST")
        .validFrom(LocalDate.of(2010, 1, 1))
        .validTo(LocalDate.of(2012, 12, 31))
        .build();

    when(servicePointService.isHighDateVersion(newServicePointVersion)).thenReturn(true);
    assertThrows(InvalidAbbreviationException.class,
        () -> servicePointController.validateAndSetAbbreviationForUpdate(existingServicePointVersion, newServicePointVersion, "TEST"));
  }

  @Test
  public void testSuccessfullAbbreviationUpdate() {
    ServicePointVersion existingServicePointVersion = ServicePointVersion.builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(2031231))
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:100016")
        .abbreviation(null)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

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
    when(servicePointService.isHighDateVersion(servicePointVersion)).thenReturn(false);

    assertDoesNotThrow(() -> servicePointController.validateAndSetAbbreviationForUpdate(existingServicePointVersion, servicePointVersion, "TEST"));
    assertEquals(servicePointVersion.getAbbreviation(), servicePointVersion.getAbbreviation());
  }
}