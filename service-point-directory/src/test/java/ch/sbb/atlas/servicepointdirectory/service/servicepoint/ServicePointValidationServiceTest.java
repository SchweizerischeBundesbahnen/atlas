package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion.ServicePointVersionBuilder;
import ch.sbb.atlas.servicepointdirectory.exception.AbbreviationUpdateNotAllowedException;
import ch.sbb.atlas.servicepointdirectory.exception.InvalidAbbreviationException;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointDesignationLongConflictException;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointDesignationOfficialConflictException;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointVersionMapper;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
 class ServicePointValidationServiceTest {

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  private final ServicePointValidationService servicePointValidationService;
  private final ServicePointVersionRepository versionRepository;

  @Autowired
   ServicePointValidationServiceTest(ServicePointValidationService servicePointValidationService,
      ServicePointVersionRepository versionRepository) {
    this.servicePointValidationService = servicePointValidationService;
    this.versionRepository = versionRepository;
  }

  @BeforeEach
  void createDefaultVersion() {
    ServicePointVersion servicePointVersion = ServicePointTestData.getBernWyleregg();
    servicePointVersion.setDesignationLong("Wyleregg, Loraine, Bern");
    servicePointVersion.setAbbreviation("TEST");
    versionRepository.save(servicePointVersion);
  }

  @AfterEach
  void clearVersions() {
    versionRepository.deleteAll();
  }

  @Test
  void shouldNotThrowExceptionOnDifferentDesignationOfficial() {
    ServicePointVersion servicePointVersion = servicePointVersionBuilder()
        .designationOfficial("Antonios Hood").build();
    assertDoesNotThrow(() -> servicePointValidationService.validateServicePointPreconditionBusinessRule(servicePointVersion));
  }

  @Test
  void shouldThrowExceptionOnSameDesignationOfficial() {
    ServicePointVersion servicePointVersion = servicePointVersionBuilder().build();
    assertThrows(ServicePointDesignationOfficialConflictException.class,
        () -> servicePointValidationService.validateServicePointPreconditionBusinessRule(servicePointVersion));
  }

  @Test
  void shouldNotThrowExceptionOnSameDesignationOfficialInDifferentCountries() {
    ServicePointVersion servicePointVersion = servicePointVersionBuilder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8014350))
        .country(Country.GERMANY)
        .build();
    assertDoesNotThrow(() -> servicePointValidationService.validateServicePointPreconditionBusinessRule(servicePointVersion));
  }

  @Test
  void shouldNotThrowExceptionOnDifferentDesignationLong() {
    ServicePointVersion servicePointVersion = servicePointVersionBuilder()
        .designationOfficial("Antonios Hood").build();
    assertDoesNotThrow(() -> servicePointValidationService.validateServicePointPreconditionBusinessRule(servicePointVersion));
  }

  @Test
  void shouldNotThrowExceptionOnSameDesignationLongInDifferentCountries() {
    ServicePointVersion servicePointVersion = servicePointVersionBuilder()
        .designationOfficial("Wyler Egg")
        .designationLong("Wyleregg, Loraine, Bern")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8014350))
        .country(Country.GERMANY)
        .build();
    assertDoesNotThrow(() -> servicePointValidationService.validateServicePointPreconditionBusinessRule(servicePointVersion));
  }

  @Test
  void shouldThrowExceptionOnSameDesignationLong() {
    ServicePointVersion servicePointVersion = servicePointVersionBuilder()
        .designationOfficial("Wyler Egg")
        .designationLong("Wyleregg, Loraine, Bern").build();
    assertThrows(ServicePointDesignationLongConflictException.class,
        () -> servicePointValidationService.validateServicePointPreconditionBusinessRule(servicePointVersion));
  }

  private ServicePointVersionBuilder<?, ?> servicePointVersionBuilder() {
    return ServicePointVersion
        .builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8589108))
        .sloid("ch:1:sloid:89108")
        .numberShort(89108)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Bern, Wyleregg")
        .abbreviation(null)
        .businessOrganisation("ch:1:sboid:100626")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .categories(new HashSet<>())
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .operatingPoint(true)
        .operatingPointWithTimetable(true);
  }

  @Test
  public void shouldNotThrowExceptionWhenAbbreviationEmpty() {
      CreateServicePointVersionModel createServicePointVersionModel = CreateServicePointVersionModel.builder()
          .numberWithoutCheckDigit(8507111)
          .designationOfficial("Bern")
          .businessOrganisation("ch:1:sboid:123456")
          .validFrom(LocalDate.of(2022, 1, 1))
          .validTo(LocalDate.of(2022, 12, 31))
          .build();

      ServicePointVersion servicePointVersion = ServicePointVersionMapper.toEntity(createServicePointVersionModel);

      assertDoesNotThrow(() -> servicePointValidationService.validateAndSetAbbreviationForCreate(servicePointVersion));
  }
    @Test
    void shouldThrowExceptionWhenServicePointBusinessOrganisationIsNotInAllowedList(){
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
            () -> servicePointValidationService.commonAbbreviationValidation(servicePointVersion));
    }

    @Test
    public void shouldThrowExceptionWhenAbbreviationIsNotUnique() {
        CreateServicePointVersionModel createServicePointVersionModel = CreateServicePointVersionModel.builder()
            .numberWithoutCheckDigit(8507111)
            .designationOfficial("Bern")
            .businessOrganisation("ch:1:sboid:100016")
            .abbreviation("TEST")
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2022, 12, 31))
            .build();

        ServicePointVersion servicePointVersion = ServicePointVersionMapper.toEntity(createServicePointVersionModel);

        assertThrows(InvalidAbbreviationException.class,
            () -> servicePointValidationService.commonAbbreviationValidation(servicePointVersion));
    }

    @Test
    public void shouldThrowExceptionWhenTryToChangeExistingAbbreviation() {
        ServicePointVersion existingServicePointVersion = versionRepository.findById(1000L).get();

        ServicePointVersion servicePointVersion = ServicePointVersion.builder()
            .number(ServicePointNumber.ofNumberWithoutCheckDigit(2031231))
            .designationOfficial("Bern")
            .businessOrganisation("ch:1:sboid:100016")
            .abbreviation("ABCD")
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2022, 12, 31))
            .build();


        assertThrows(AbbreviationUpdateNotAllowedException.class,
            () -> servicePointValidationService.validateAndSetAbbreviationForUpdate(existingServicePointVersion, servicePointVersion));
    }

    @Test
    public void shouldThrowExceptionWhenAddingAbbreviationToServicePointWithNonHighestDate() {
        ServicePointVersion existingServicePointVersion = versionRepository.findById(1000L).get();

        ServicePointVersion servicePointVersion = ServicePointVersion.builder()
            .number(existingServicePointVersion.getNumber())
            .designationOfficial("Wylerggggg 1")
            .businessOrganisation("ch:1:sboid:100016")
            .abbreviation("TEST")
            .validFrom(LocalDate.of(2005, 1, 1))
            .validTo(LocalDate.of(2006, 12, 31))
            .build();

        assertThrows(InvalidAbbreviationException.class,
            () -> servicePointValidationService.validateAndSetAbbreviationForUpdate(existingServicePointVersion, servicePointVersion));
    }

    @Test
    public void shouldSuccessfullyAddAbbreviationAndUpdateServicePoint() {

        versionRepository.deleteAll();

        ServicePointVersion servicePointVersion = ServicePointTestData.getBernWyleregg();
        servicePointVersion.setBusinessOrganisation("ch:1:sboid:100016");

        versionRepository.save(servicePointVersion);

        ServicePointVersion existingServicePoint = versionRepository.findById(1001L).get();


        ServicePointVersion editedVersion = ServicePointVersion.builder()
            .number(servicePointVersion.getNumber())
            .designationOfficial(servicePointVersion.getDesignationOfficial())
            .businessOrganisation(servicePointVersion.getBusinessOrganisation())
            .abbreviation("ABCD")
            .validFrom(servicePointVersion.getValidFrom())
            .validTo(servicePointVersion.getValidTo())
            .build();


        assertDoesNotThrow(() -> servicePointValidationService.validateAndSetAbbreviationForUpdate(existingServicePoint, editedVersion));
    }
}