package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.TrafficPointUpdateCsvModel;
import ch.sbb.atlas.imports.bulk.TrafficPointUpdateCsvModel.Fields;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.user.administration.security.service.CountryAndBusinessOrganisationBasedUserAdministrationService;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
class TrafficPointBulkImportServiceTest {

  @MockBean
  private CountryAndBusinessOrganisationBasedUserAdministrationService administrationService;

  @Autowired
  private TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  @Autowired
  private ServicePointVersionRepository servicePointVersionRepository;

  @Autowired
  private TrafficPointBulkImportService trafficPointBulkImportService;

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

    trafficPointBulkImportService.updateTrafficPoint(BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
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
    trafficPointBulkImportService.updateTrafficPointByUserName("e123456",
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

    trafficPointBulkImportService.updateTrafficPoint(BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
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

    trafficPointBulkImportService.updateTrafficPoint(BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
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
    ThrowingCallable update = () -> trafficPointBulkImportService.updateTrafficPoint(
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
  void shouldUpdateDesignationWhenGeolocationWasNull() {
    bernWylereggPlatform.setTrafficPointElementGeolocation(null);
    TrafficPointElementVersion trafficPointElementVersion = trafficPointElementVersionRepository.save(bernWylereggPlatform);
    assertThat(trafficPointElementVersion.hasGeolocation()).isFalse();

    trafficPointBulkImportService.updateTrafficPoint(BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
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

    trafficPointBulkImportService.updateTrafficPoint(BulkImportUpdateContainer.<TrafficPointUpdateCsvModel>builder()
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

}
