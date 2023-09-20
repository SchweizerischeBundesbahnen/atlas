package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion.ServicePointVersionBuilder;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointDesignationLongConflictException;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointDesignationOfficialConflictException;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.time.LocalDate;
import java.util.HashSet;
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
}