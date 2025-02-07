package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import static ch.sbb.atlas.servicepointdirectory.ServicePointTestData.WYLEREGG_NUMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.TrafficPointUpdateCsvModel;
import ch.sbb.atlas.imports.model.TrafficPointUpdateCsvModel.Fields;
import ch.sbb.atlas.imports.model.create.TrafficPointCreateCsvModel;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.SloidNotValidException;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.bulk.TrafficPointElementBulkImportService;
import ch.sbb.atlas.user.administration.security.service.CountryAndBusinessOrganisationBasedUserAdministrationService;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@IntegrationTest
class TrafficPointElementBulkImportServiceTest {

  @MockitoBean
  private CountryAndBusinessOrganisationBasedUserAdministrationService administrationService;

  @MockitoBean
  private GeoReferenceService geoReferenceService;

  @MockitoBean
  private LocationService locationService;

  @Autowired
  private TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  @Autowired
  private ServicePointVersionRepository servicePointVersionRepository;

  @Autowired
  private TrafficPointElementBulkImportService trafficPointElementBulkImportService;

  private TrafficPointElementVersion bernWylereggPlatform;

  private static final String BERN_DESIGNATION = "Bern designation";

  @BeforeEach
  void setUp() {
    doReturn(true).when(administrationService).hasUserPermissionsToCreateOrEditServicePointDependentObject(any(), any());
    bernWylereggPlatform = trafficPointElementVersionRepository.save(TrafficPointTestData.getWylerEggPlatform());
    servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
  }

  @AfterEach
  void tearDown() {
    trafficPointElementVersionRepository.deleteAll();
    servicePointVersionRepository.deleteAll();
  }

  @Test
  void shouldUpdateBulkAddingProperty() {
    assertThat(bernWylereggPlatform.getDesignation()).isNull();

    trafficPointElementBulkImportService.updateTrafficPoint(BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
            .object(TrafficPointUpdateCsvModel.builder()
                .sloid(bernWylereggPlatform.getSloid())
                .validFrom(bernWylereggPlatform.getValidFrom())
                .validTo(bernWylereggPlatform.getValidTo())
                .designation(BERN_DESIGNATION)
                .build())
        .build());
    TrafficPointElementVersion trafficPointElementVersion =
        trafficPointElementVersionRepository.findById(bernWylereggPlatform.getId()).orElseThrow();
    assertThat(trafficPointElementVersion.getDesignation()).isEqualTo(BERN_DESIGNATION);
  }

  @Test
  void shouldUpdateBulkWithUserInNameOf() {
    trafficPointElementBulkImportService.updateTrafficPointByUserName("e123456",
        BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
            .object(TrafficPointUpdateCsvModel.builder()
                .sloid(bernWylereggPlatform.getSloid())
                .validFrom(bernWylereggPlatform.getValidFrom())
                .validTo(bernWylereggPlatform.getValidTo())
                .designation(BERN_DESIGNATION)
                .build())
            .build());

    TrafficPointElementVersion trafficPointElementVersion =
        trafficPointElementVersionRepository.findById(bernWylereggPlatform.getId()).orElseThrow();
    assertThat(trafficPointElementVersion.getDesignation()).isEqualTo(BERN_DESIGNATION);
  }

  @Test
  void shouldUpdateBulkRemovingProperty() {
    assertThat(bernWylereggPlatform.getTrafficPointElementGeolocation().getHeight()).isEqualTo(555.98);

    trafficPointElementBulkImportService.updateTrafficPoint(BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
        .object(TrafficPointUpdateCsvModel.builder()
            .sloid(bernWylereggPlatform.getSloid())
            .validFrom(bernWylereggPlatform.getValidFrom())
            .validTo(bernWylereggPlatform.getValidTo())
            .build())
        .attributesToNull(List.of(Fields.height))
        .build());

    TrafficPointElementVersion trafficPointElementVersion =
        trafficPointElementVersionRepository.findById(bernWylereggPlatform.getId()).orElseThrow();
    assertThat(trafficPointElementVersion.getTrafficPointElementGeolocation().getHeight()).isNull();
  }

  @Test
  void shouldUpdateAndGetMoreVersions() {
    assertThat(trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(bernWylereggPlatform.getSloid()))
        .hasSize(1);

    trafficPointElementBulkImportService.updateTrafficPoint(BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
        .object(TrafficPointUpdateCsvModel.builder()
            .sloid(bernWylereggPlatform.getSloid())
            .validFrom(LocalDate.of(2023, 1, 1))
            .validTo(LocalDate.of(2023, 6, 30))
            .designation("BERN")
            .build())
        .build());

    List<TrafficPointElementVersion> versions =
        trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(bernWylereggPlatform.getSloid());
    assertThat(versions).hasSize(3);

    TrafficPointElementVersion firstVersion = versions.getFirst();
    assertThat(firstVersion.getValidFrom()).isEqualTo(LocalDate.of(2022,  1, 1));
    assertThat(firstVersion.getValidTo()).isEqualTo(LocalDate.of(2022,  12, 31));
    assertThat(firstVersion.getDesignation()).isNull();

    TrafficPointElementVersion secondVersion = versions.get(1);
    assertThat(secondVersion.getValidFrom()).isEqualTo(LocalDate.of(2023,  1, 1));
    assertThat(secondVersion.getValidTo()).isEqualTo(LocalDate.of(2023,  6, 30));
    assertThat(secondVersion.getDesignation()).isEqualTo("BERN");

    TrafficPointElementVersion thirdVersion = versions.getLast();
    assertThat(thirdVersion.getValidFrom()).isEqualTo(LocalDate.of(2023,  7, 1));
    assertThat(thirdVersion.getValidTo()).isEqualTo(LocalDate.of(2024,  1, 1));
    assertThat(thirdVersion.getDesignation()).isNull();
  }

  @Test
  void shouldThrowSloidNotFoundException() {
    ThrowingCallable update = () -> trafficPointElementBulkImportService.updateTrafficPoint(
        BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
            .object(TrafficPointUpdateCsvModel.builder()
                .sloid("unknown:sloid")
                .validFrom(LocalDate.of(2023, 1, 1))
                .validTo(LocalDate.of(2023, 6, 30))
                .designation(BERN_DESIGNATION)
                .build())
            .build());
    assertThatExceptionOfType(SloidNotFoundException.class).isThrownBy(update);
  }

  @Test
  void shouldThrowIllegalStateException() {
    ThrowingCallable update = () -> trafficPointElementBulkImportService.updateTrafficPoint(
        BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
            .object(TrafficPointUpdateCsvModel.builder()
                .validFrom(LocalDate.of(2023, 1, 1))
                .validTo(LocalDate.of(2023, 6, 30))
                .designation(BERN_DESIGNATION)
                .build())
            .build());
    assertThatExceptionOfType(IllegalStateException.class).isThrownBy(update);
  }

  @Test
  void shouldThrowServicePointNumberNotFoundException() {
    servicePointVersionRepository.deleteAll();
    ThrowingCallable update = () -> trafficPointElementBulkImportService.updateTrafficPoint(
        BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
            .object(TrafficPointUpdateCsvModel.builder()
                .sloid(bernWylereggPlatform.getSloid())
                .validFrom(LocalDate.of(2023, 1, 1))
                .validTo(LocalDate.of(2023, 6, 30))
                .designation(BERN_DESIGNATION)
                .build())
            .build());
    assertThatExceptionOfType(ServicePointNumberNotFoundException.class).isThrownBy(update);
  }

  @Test
  void shouldUpdateDesignationWhenGeolocationWasNull() {
    bernWylereggPlatform.setTrafficPointElementGeolocation(null);
    TrafficPointElementVersion trafficPointElementVersion = trafficPointElementVersionRepository.save(bernWylereggPlatform);
    assertThat(trafficPointElementVersion.hasGeolocation()).isFalse();

    trafficPointElementBulkImportService.updateTrafficPoint(BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
        .object(TrafficPointUpdateCsvModel.builder()
            .sloid(bernWylereggPlatform.getSloid())
            .validFrom(bernWylereggPlatform.getValidFrom())
            .validTo(bernWylereggPlatform.getValidTo())
            .designation(BERN_DESIGNATION)
            .build())
        .build());

    TrafficPointElementVersion bulkUpdateResult =
        trafficPointElementVersionRepository.findById(trafficPointElementVersion.getId()).orElseThrow();
    assertThat(bulkUpdateResult.getDesignation()).isEqualTo(BERN_DESIGNATION);
    assertThat(bulkUpdateResult.hasGeolocation()).isFalse();
  }

  @Test
  void shouldUpdateGeolocationWhenGeolocationWasNull() {
    bernWylereggPlatform.setTrafficPointElementGeolocation(null);
    TrafficPointElementVersion trafficPointElementVersion = trafficPointElementVersionRepository.save(bernWylereggPlatform);
    assertThat(trafficPointElementVersion.hasGeolocation()).isFalse();

    trafficPointElementBulkImportService.updateTrafficPoint(BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
        .object(TrafficPointUpdateCsvModel.builder()
            .sloid(bernWylereggPlatform.getSloid())
            .validFrom(bernWylereggPlatform.getValidFrom())
            .validTo(bernWylereggPlatform.getValidTo())
            .east(2604525.0)
            .north(1259900.0)
            .spatialReference(SpatialReference.LV95)
            .build())
        .build());

    TrafficPointElementVersion bulkUpdateResult =
        trafficPointElementVersionRepository.findById(trafficPointElementVersion.getId()).orElseThrow();
    assertThat(bulkUpdateResult.hasGeolocation()).isTrue();
  }

  @Test
  void shouldCreateTrafficPointElementGeneratingSloid() {
    String generatedSloid = "ch:1:sloid:89008:0:123";
    when(locationService.generateTrafficPointSloid(eq(TrafficPointElementType.BOARDING_PLATFORM),
        any(ServicePointNumber.class))).thenReturn(generatedSloid);

    trafficPointElementBulkImportService.createTrafficPoint(BulkImportUpdateContainer.<TrafficPointCreateCsvModel>builder()
        .object(TrafficPointCreateCsvModel.builder()
            .sloid(null)
            .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
            .validFrom(bernWylereggPlatform.getValidFrom())
            .validTo(bernWylereggPlatform.getValidTo())
            .number(WYLEREGG_NUMBER)
            .designation("WylereggLade")
            .build())
        .build());

    TrafficPointElementVersion trafficPointElementVersion = trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(
        generatedSloid).getFirst();
    assertThat(trafficPointElementVersion.getSloid()).isNotNull().isEqualTo(generatedSloid);
  }

  @Test
  void shouldCreateTrafficPointElementClaimingSloid() {
    String chosenSloid = "ch:1:sloid:89008:65:123456";

    trafficPointElementBulkImportService.createTrafficPoint(BulkImportUpdateContainer.<TrafficPointCreateCsvModel>builder()
        .object(TrafficPointCreateCsvModel.builder()
            .sloid(chosenSloid)
            .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
            .validFrom(bernWylereggPlatform.getValidFrom())
            .validTo(bernWylereggPlatform.getValidTo())
            .number(WYLEREGG_NUMBER)
            .designation("WylereggLade")
            .build())
        .build());

    verify(locationService).claimSloid(SloidType.PLATFORM, chosenSloid);

    TrafficPointElementVersion trafficPointElementVersion = trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(
        chosenSloid).getFirst();
    assertThat(trafficPointElementVersion.getSloid()).isNotNull().isEqualTo(chosenSloid);
  }

  @Test
  void shouldFailValidationOnSloidWithWrongPrefixAndNotClaimSloid() {
    assertThatExceptionOfType(SloidNotValidException.class).isThrownBy(
        () -> trafficPointElementBulkImportService.createTrafficPoint(
            BulkImportUpdateContainer.<TrafficPointCreateCsvModel>builder()
                .object(TrafficPointCreateCsvModel.builder()
                    .sloid("ch:1:sloid:7000:0:123456")
                    .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
                    .validFrom(bernWylereggPlatform.getValidFrom())
                    .validTo(bernWylereggPlatform.getValidTo())
                    .number(WYLEREGG_NUMBER)
                    .designation("WylereggLade")
                    .build())
                .build())).withMessage("The SLOID ch:1:sloid:7000:0:123456 is not valid due to: did not start with ch:1:sloid:89008");

    verifyNoInteractions(locationService);
  }

  @Test
  void shouldCreateTrafficPointElementByUsername() {
    String generatedSloid = "ch:1:sloid:89008:0:123";
    when(locationService.generateTrafficPointSloid(eq(TrafficPointElementType.BOARDING_PLATFORM),
        any(ServicePointNumber.class))).thenReturn(generatedSloid);

    BulkImportUpdateContainer<TrafficPointCreateCsvModel> container =
        BulkImportUpdateContainer.<TrafficPointCreateCsvModel>builder()
        .object(TrafficPointCreateCsvModel.builder()
            .sloid(null)
            .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
            .validFrom(bernWylereggPlatform.getValidFrom())
            .validTo(bernWylereggPlatform.getValidTo())
            .number(WYLEREGG_NUMBER)
            .designation("WylereggLade")
            .build())
        .build();
    trafficPointElementBulkImportService.createTrafficPointByUserName("e123456", container);

    TrafficPointElementVersion trafficPointElementVersion = trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(
        generatedSloid).getFirst();
    assertThat(trafficPointElementVersion.getSloid()).isNotNull().isEqualTo(generatedSloid);
  }

}
