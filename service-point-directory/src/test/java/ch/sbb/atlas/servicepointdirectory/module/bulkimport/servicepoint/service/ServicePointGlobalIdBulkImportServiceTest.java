package ch.sbb.atlas.servicepointdirectory.module.bulkimport.servicepoint.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.ServicePointGlobalIdUpdateCsvModel;
import ch.sbb.atlas.imports.model.ServicePointGlobalIdUpdateCsvModel.Fields;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.module.bulkimport.servicepoint.exception.GlobalIdBulkImportInfoException;
import ch.sbb.atlas.servicepointdirectory.module.geodata.service.ServicePointGeoDataService;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointGlobalId;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.InvalidGlobalIdException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.ServicePointIdentifierMismatchException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.repository.ServicePointGlobalIdRepository;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.repository.ServicePointVersionRepository;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.atlas.user.administration.security.service.CountryAndBusinessOrganisationBasedUserAdministrationService;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * The Global-ID bulk import scenario.
 *
 * <p>The behaviour worth pinning here is the deliberate difference to the interactive endpoint: a Global-ID already held by
 * another service point is <b>taken away</b> rather than refused, and the displaced service point is named in the protocol.
 */
@IntegrationTest
class ServicePointGlobalIdBulkImportServiceTest {

  private static final Integer GERMAN_NUMBER = 1105770;
  private static final Integer OTHER_GERMAN_NUMBER = 1105771;
  private static final String GLOBAL_ID = "de:05770:1282";

  @MockitoBean
  private CountryAndBusinessOrganisationBasedUserAdministrationService administrationService;

  @MockitoBean
  private BusinessOrganisationBasedUserAdministrationService businessOrganisationBasedUserAdministrationService;

  @MockitoBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @MockitoBean
  private LocationService locationService;

  @MockitoBean
  private ServicePointGeoDataService servicePointGeoDataService;

  @Autowired
  private ServicePointVersionRepository servicePointVersionRepository;

  @Autowired
  private ServicePointGlobalIdRepository servicePointGlobalIdRepository;

  @Autowired
  private ServicePointBulkImportService servicePointBulkImportService;

  private ServicePointVersion german;
  private ServicePointVersion otherGerman;

  @BeforeEach
  void setUp() {
    doReturn(true).when(administrationService).hasUserPermissionsToUpdateCountryBased(any(), any(), any());
    doReturn(true).when(businessOrganisationBasedUserAdministrationService).isAtLeastSupervisor(any());

    german = servicePointVersionRepository.save(germanServicePoint(GERMAN_NUMBER, "ch:1:sloid:5770", "Böblingen"));
    otherGerman = servicePointVersionRepository.save(germanServicePoint(OTHER_GERMAN_NUMBER, "ch:1:sloid:5771", "Sindelfingen"));
  }

  @AfterEach
  void tearDown() {
    servicePointGlobalIdRepository.deleteAll();
    servicePointVersionRepository.deleteAll();
  }

  private static ServicePointVersion germanServicePoint(Integer number, String sloid, String name) {
    return ServicePointVersion.builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(number))
        .sloid(sloid)
        .numberShort(number % 100000)
        .country(Country.GERMANY_BUS)
        .designationOfficial(name)
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .stopPointType(StopPointType.ORDERLY)
        .businessOrganisation("ch:1:sboid:100626")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2020, Month.JANUARY, 1))
        .validTo(LocalDate.of(2099, Month.DECEMBER, 31))
        .categories(new HashSet<>())
        .operatingPoint(true)
        .operatingPointWithTimetable(true)
        .build();
  }

  private static BulkImportUpdateContainer<ServicePointGlobalIdUpdateCsvModel> container(String sloid, Integer number,
      String globalId, boolean nulled) {
    return BulkImportUpdateContainer.<ServicePointGlobalIdUpdateCsvModel>builder()
        .object(ServicePointGlobalIdUpdateCsvModel.builder().sloid(sloid).number(number).globalId(globalId).build())
        .attributesToNull(nulled ? List.of(Fields.globalId) : List.of())
        .build();
  }

  @Test
  void shouldAssignGlobalIdByNumberAlone() {
    servicePointBulkImportService.updateGlobalId(container(null, GERMAN_NUMBER, GLOBAL_ID, false));

    assertThat(servicePointGlobalIdRepository.findByGlobalId(GLOBAL_ID)).get()
        .extracting(mapping -> mapping.getServicePointNumber().getNumber())
        .isEqualTo(german.getNumber().getNumber());
  }

  @Test
  void shouldAssignGlobalIdBySloidAlone() {
    servicePointBulkImportService.updateGlobalId(container(german.getSloid(), null, GLOBAL_ID, false));

    assertThat(servicePointGlobalIdRepository.findByGlobalId(GLOBAL_ID)).isPresent();
  }

  @Test
  void shouldRefuseLineWhereSloidAndNumberDescribeDifferentServicePoints() {
    BulkImportUpdateContainer<ServicePointGlobalIdUpdateCsvModel> container = container(otherGerman.getSloid(), GERMAN_NUMBER,
        GLOBAL_ID, false);
    assertThatExceptionOfType(ServicePointIdentifierMismatchException.class).isThrownBy(
        () -> servicePointBulkImportService.updateGlobalId(
            container));
  }

  @Test
  void shouldTakeGlobalIdAwayFromItsCurrentHolderAndReportIt() {
    // Given
    servicePointBulkImportService.updateGlobalId(container(null, OTHER_GERMAN_NUMBER, GLOBAL_ID, false));

    // When
    GlobalIdBulkImportInfoException info = catchThrowableOfType(GlobalIdBulkImportInfoException.class,
        () -> servicePointBulkImportService.updateGlobalId(container(null, GERMAN_NUMBER, GLOBAL_ID, false)));

    // Then
    assertThat(info.getCode()).isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_INFO.REPOINTED");
    assertThat(servicePointGlobalIdRepository.findByServicePointNumber(otherGerman.getNumber())).isEmpty();
    assertThat(servicePointGlobalIdRepository.findByServicePointNumber(german.getNumber())).get()
        .extracting(ServicePointGlobalId::getGlobalId).isEqualTo(GLOBAL_ID);
  }

  @Test
  void shouldReportRepointingAsInfoSoThatItSurvivesIntoTheImportLog() {
    // Given
    servicePointBulkImportService.updateGlobalId(container(null, OTHER_GERMAN_NUMBER, GLOBAL_ID, false));

    // When
    GlobalIdBulkImportInfoException info = catchThrowableOfType(GlobalIdBulkImportInfoException.class,
        () -> servicePointBulkImportService.updateGlobalId(container(null, GERMAN_NUMBER, GLOBAL_ID, false)));

    // Then
    assertThat(info.getErrorResponse().getStatus()).isEqualTo(ErrorResponse.VERSIONING_NO_CHANGES_HTTP_STATUS);
    assertThat(info.getErrorResponse().getDetails()).singleElement()
        .satisfies(detail -> {
          assertThat(detail.getDisplayInfo().getCode()).isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_INFO.REPOINTED");
          assertThat(detail.getDisplayInfo().getParameters()).extracting("key")
              .containsExactlyInAnyOrder("globalId", "servicePointNumber");
        });
  }

  @Test
  void shouldNameTheDisplacedServicePointInTheReport() {
    // Given
    servicePointBulkImportService.updateGlobalId(container(null, OTHER_GERMAN_NUMBER, GLOBAL_ID, false));

    // When
    GlobalIdBulkImportInfoException info = catchThrowableOfType(GlobalIdBulkImportInfoException.class,
        () -> servicePointBulkImportService.updateGlobalId(container(null, GERMAN_NUMBER, GLOBAL_ID, false)));

    // Then
    assertThat(info.getDisplayInfo().getParameters()).extracting("key", "value")
        .containsExactlyInAnyOrder(tuple("globalId", GLOBAL_ID),
            tuple("servicePointNumber", String.valueOf(otherGerman.getNumber().getNumber())));
  }

  @Test
  void shouldBeIdempotentWhenTheServicePointAlreadyHoldsTheGlobalId() {
    servicePointBulkImportService.updateGlobalId(container(null, GERMAN_NUMBER, GLOBAL_ID, false));

    assertThatNoException().isThrownBy(
        () -> servicePointBulkImportService.updateGlobalId(container(null, GERMAN_NUMBER, GLOBAL_ID, false)));

    assertThat(servicePointGlobalIdRepository.findAll()).hasSize(1);
  }

  @Test
  void shouldRemoveGlobalIdOnExplicitNulling() {
    servicePointBulkImportService.updateGlobalId(container(null, GERMAN_NUMBER, GLOBAL_ID, false));

    GlobalIdBulkImportInfoException info = catchThrowableOfType(GlobalIdBulkImportInfoException.class,
        () -> servicePointBulkImportService.updateGlobalId(container(null, GERMAN_NUMBER, null, true)));

    assertThat(info.getCode()).isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_INFO.REMOVED");
    assertThat(info.getErrorResponse().getStatus()).isEqualTo(ErrorResponse.VERSIONING_NO_CHANGES_HTTP_STATUS);
    assertThat(servicePointGlobalIdRepository.findByServicePointNumber(german.getNumber())).isEmpty();
  }

  @Test
  void shouldSayNothingWhenNullingAServicePointThatHasNoGlobalId() {
    assertThatNoException().isThrownBy(
        () -> servicePointBulkImportService.updateGlobalId(container(null, GERMAN_NUMBER, null, true)));
  }

  @Test
  void shouldRefuseBlankGlobalIdRatherThanSilentlyUnassignIt() {
    servicePointBulkImportService.updateGlobalId(container(null, GERMAN_NUMBER, GLOBAL_ID, false));

    BulkImportUpdateContainer<ServicePointGlobalIdUpdateCsvModel> container = container(null, GERMAN_NUMBER, null, false);
    assertThatExceptionOfType(InvalidGlobalIdException.class).isThrownBy(
        () -> servicePointBulkImportService.updateGlobalId(container));

    assertThat(servicePointGlobalIdRepository.findByServicePointNumber(german.getNumber())).isPresent();
  }

  @Test
  void shouldDeriveTheCountryPrefixFromTheServicePointNumberRatherThanTrustTheFile() {
    BulkImportUpdateContainer<ServicePointGlobalIdUpdateCsvModel> container = container(null, GERMAN_NUMBER, "at:47:1234", false);
    assertThatExceptionOfType(InvalidGlobalIdException.class).isThrownBy(
        () -> servicePointBulkImportService.updateGlobalId(container));
  }
}
