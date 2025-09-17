package ch.sbb.prm.directory.module.bulkimport.plaform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.PlatformCompleteUpdateCsvModel;
import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel;
import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel.Fields;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.prm.directory.module.bulkimport.service.PlatformBulkImportService;
import ch.sbb.prm.directory.module.platform.PlatformTestData;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.platform.repository.PlatformRepository;
import ch.sbb.prm.directory.module.stoppoint.StopPointTestData;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import ch.sbb.prm.directory.module.stoppoint.repository.StopPointRepository;
import ch.sbb.prm.directory.security.PrmUserAdministrationService;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@IntegrationTest
class PlatformBulkImportServiceTest {

  @MockitoBean
  private PrmUserAdministrationService prmUserAdministrationService;

  @Autowired
  private PlatformRepository platformRepository;

  @Autowired
  private StopPointRepository stopPointRepository;

  @Autowired
  private PlatformBulkImportService platformBulkImportService;

  private PlatformVersion platformVersionReduced;

  private PlatformVersion platformVersionComplete;

  private static final String ADDITIONAL_INFORMATION = "Additional information";

  @BeforeEach
  void setUp() {
    doReturn(true).when(prmUserAdministrationService).hasUserRightsToCreateOrEditPrmObject(any());
    platformVersionReduced = PlatformTestData.getReducedPlatformVersion();
    platformVersionComplete = PlatformTestData.getCompletePlatformVersion();
    platformRepository.save(platformVersionReduced);
    platformRepository.save(platformVersionComplete);
    StopPointVersion stopPointVersion = StopPointTestData.builderVersionReduced().build();
    stopPointRepository.save(stopPointVersion);
  }

  @AfterEach
  void tearDown() {
    platformRepository.deleteAll();
    stopPointRepository.deleteAll();
  }

  @Test
  void shouldUpdateBulkAddingProperty() {
    assertThat(platformVersionReduced.getAdditionalInformation()).isNull();

    platformBulkImportService.updatePlatformReduced(BulkImportUpdateContainer.<PlatformReducedUpdateCsvModel>builder()
        .object(PlatformReducedUpdateCsvModel.builder()
            .sloid(platformVersionReduced.getSloid())
            .validFrom(platformVersionReduced.getValidFrom())
            .validTo(platformVersionReduced.getValidTo())
            .additionalInformation(ADDITIONAL_INFORMATION)
            .build())
        .build());
    PlatformVersion platform =
        platformRepository.findById(platformVersionReduced.getId()).orElseThrow();
    assertThat(platform.getAdditionalInformation()).isEqualTo(ADDITIONAL_INFORMATION);
  }

  @Test
  void shouldUpdateBulkWithUserInNameOf() {
    platformBulkImportService.updatePlatformReducedByUsername("e123456",
        BulkImportUpdateContainer.<PlatformReducedUpdateCsvModel>builder()
            .object(PlatformReducedUpdateCsvModel.builder()
                .sloid(platformVersionReduced.getSloid())
                .validFrom(platformVersionReduced.getValidFrom())
                .validTo(platformVersionReduced.getValidTo())
                .additionalInformation(ADDITIONAL_INFORMATION)
                .build())
            .build());

    PlatformVersion platformVersion1 =
        platformRepository.findById(platformVersionReduced.getId()).orElseThrow();
    assertThat(platformVersion1.getAdditionalInformation()).isEqualTo(ADDITIONAL_INFORMATION);
  }

  @Test
  void shouldUpdateBulkRemovingProperty() {
    assertThat(platformVersionReduced.getHeight()).isEqualTo(123.12);

    platformBulkImportService.updatePlatformReduced(BulkImportUpdateContainer.<PlatformReducedUpdateCsvModel>builder()
        .object(PlatformReducedUpdateCsvModel.builder()
            .sloid(platformVersionReduced.getSloid())
            .validFrom(platformVersionReduced.getValidFrom())
            .validTo(platformVersionReduced.getValidTo())
            .build())
        .attributesToNull(List.of(Fields.height, Fields.additionalInformation, Fields.inclinationLongitudinal))
        .build());

    PlatformVersion platformVersion1 =
        platformRepository.findById(platformVersionReduced.getId()).orElseThrow();
    assertThat(platformVersion1.getHeight()).isNull();
  }

  @Test
  void shouldUpdateAndGetMoreVersions() {
    assertThat(platformRepository.findAllBySloidOrderByValidFrom(platformVersionReduced.getSloid())).hasSize(1);

    platformBulkImportService.updatePlatformReduced(BulkImportUpdateContainer.<PlatformReducedUpdateCsvModel>builder()
        .object(PlatformReducedUpdateCsvModel.builder()
            .sloid(platformVersionReduced.getSloid())
            .validFrom(LocalDate.of(2000, 4, 1))
            .validTo(LocalDate.of(2000, 7, 31))
            .additionalInformation(ADDITIONAL_INFORMATION)
            .build())
        .build());

    List<PlatformVersion> versions =
        platformRepository.findAllBySloidOrderByValidFrom(platformVersionReduced.getSloid());
    assertThat(versions).hasSize(3);

    PlatformVersion firstVersion = versions.getFirst();
    assertThat(firstVersion.getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(firstVersion.getValidTo()).isEqualTo(LocalDate.of(2000, 3, 31));
    assertThat(firstVersion.getAdditionalInformation()).isNull();

    PlatformVersion secondVersion = versions.get(1);
    assertThat(secondVersion.getValidFrom()).isEqualTo(LocalDate.of(2000, 4, 1));
    assertThat(secondVersion.getValidTo()).isEqualTo(LocalDate.of(2000, 7, 31));
    assertThat(secondVersion.getAdditionalInformation()).isEqualTo(ADDITIONAL_INFORMATION);

    PlatformVersion thirdVersion = versions.getLast();
    assertThat(thirdVersion.getValidFrom()).isEqualTo(LocalDate.of(2000, 8, 1));
    assertThat(thirdVersion.getValidTo()).isEqualTo(LocalDate.of(2000, 12, 31));
    assertThat(thirdVersion.getAdditionalInformation()).isNull();
  }

  @Test
  void shouldThrowSloidNotFoundException() {
    ThrowingCallable update = () -> platformBulkImportService.updatePlatformReduced(
        BulkImportUpdateContainer.<PlatformReducedUpdateCsvModel>builder()
            .object(PlatformReducedUpdateCsvModel.builder()
                .sloid("unknown:sloid")
                .validFrom(LocalDate.of(2023, 1, 1))
                .validTo(LocalDate.of(2023, 6, 30))
                .additionalInformation(ADDITIONAL_INFORMATION)
                .build())
            .build());
    assertThatExceptionOfType(SloidNotFoundException.class).isThrownBy(update);
  }

  @Test
  void shouldThrowIllegalStateException() {
    ThrowingCallable update = () -> platformBulkImportService.updatePlatformReduced(
        BulkImportUpdateContainer.<PlatformReducedUpdateCsvModel>builder()
            .object(PlatformReducedUpdateCsvModel.builder()
                .validFrom(LocalDate.of(2023, 1, 1))
                .validTo(LocalDate.of(2023, 6, 30))
                .additionalInformation(ADDITIONAL_INFORMATION)
                .build())
            .build());
    assertThatExceptionOfType(IllegalStateException.class).isThrownBy(update);
  }

  @Test
  void shouldUpdateBulkAddingPropertyComplete() {
    assertThat(platformVersionComplete.getAdditionalInformation()).isNull();

    platformBulkImportService.updatePlatformComplete(BulkImportUpdateContainer.<PlatformCompleteUpdateCsvModel>builder()
        .object(PlatformCompleteUpdateCsvModel.builder()
            .sloid(platformVersionComplete.getSloid())
            .validFrom(platformVersionComplete.getValidFrom())
            .validTo(platformVersionComplete.getValidTo())
            .additionalInformation(ADDITIONAL_INFORMATION)
            .build())
        .build());
    PlatformVersion platform =
        platformRepository.findById(platformVersionReduced.getId()).orElseThrow();
    assertThat(platform.getAdditionalInformation()).isEqualTo(ADDITIONAL_INFORMATION);
  }

  @Test
  void shouldUpdateBulkWithUserInNameOfComplete() {
    platformBulkImportService.updatePlatformCompleteByUsername("e123456",
        BulkImportUpdateContainer.<PlatformCompleteUpdateCsvModel>builder()
            .object(PlatformCompleteUpdateCsvModel.builder()
                .sloid(platformVersionComplete.getSloid())
                .validFrom(platformVersionComplete.getValidFrom())
                .validTo(platformVersionComplete.getValidTo())
                .additionalInformation(ADDITIONAL_INFORMATION)
                .build())
            .build());

    PlatformVersion platformVersion1 =
        platformRepository.findById(platformVersionReduced.getId()).orElseThrow();
    assertThat(platformVersion1.getAdditionalInformation()).isEqualTo(ADDITIONAL_INFORMATION);
  }

  @Test
  void shouldUpdateBulkRemovingPropertyComplete() {
    assertThat(platformVersionComplete.getSuperelevation()).isEqualTo(321.123);

    platformBulkImportService.updatePlatformComplete(BulkImportUpdateContainer.<PlatformCompleteUpdateCsvModel>builder()
        .object(PlatformCompleteUpdateCsvModel.builder()
            .sloid(platformVersionComplete.getSloid())
            .validFrom(platformVersionComplete.getValidFrom())
            .validTo(platformVersionComplete.getValidTo())
            .build())
        .attributesToNull(List.of(Fields.height, Fields.additionalInformation, Fields.inclinationLongitudinal))
        .build());

    PlatformVersion platformVersion1 =
        platformRepository.findById(platformVersionComplete.getId()).orElseThrow();
    assertThat(platformVersion1.getHeight()).isNull();
  }

  @Test
  void shouldUpdateAndGetMoreVersionsComplete() {
    assertThat(platformRepository.findAllBySloidOrderByValidFrom(platformVersionComplete.getSloid())).hasSize(1);

    platformBulkImportService.updatePlatformComplete(BulkImportUpdateContainer.<PlatformCompleteUpdateCsvModel>builder()
        .object(PlatformCompleteUpdateCsvModel.builder()
            .sloid(platformVersionComplete.getSloid())
            .validFrom(LocalDate.of(2000, 4, 1))
            .validTo(LocalDate.of(2000, 7, 31))
            .additionalInformation(ADDITIONAL_INFORMATION)
            .build())
        .build());

    List<PlatformVersion> versions =
        platformRepository.findAllBySloidOrderByValidFrom(platformVersionComplete.getSloid());
    assertThat(versions).hasSize(3);

    PlatformVersion firstVersion = versions.getFirst();
    assertThat(firstVersion.getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(firstVersion.getValidTo()).isEqualTo(LocalDate.of(2000, 3, 31));
    assertThat(firstVersion.getAdditionalInformation()).isNull();

    PlatformVersion secondVersion = versions.get(1);
    assertThat(secondVersion.getValidFrom()).isEqualTo(LocalDate.of(2000, 4, 1));
    assertThat(secondVersion.getValidTo()).isEqualTo(LocalDate.of(2000, 7, 31));
    assertThat(secondVersion.getAdditionalInformation()).isEqualTo(ADDITIONAL_INFORMATION);

    PlatformVersion thirdVersion = versions.getLast();
    assertThat(thirdVersion.getValidFrom()).isEqualTo(LocalDate.of(2000, 8, 1));
    assertThat(thirdVersion.getValidTo()).isEqualTo(LocalDate.of(2000, 12, 31));
    assertThat(thirdVersion.getAdditionalInformation()).isNull();
  }

  @Test
  void shouldThrowSloidNotFoundExceptionComplete() {
    ThrowingCallable update = () -> platformBulkImportService.updatePlatformComplete(
        BulkImportUpdateContainer.<PlatformCompleteUpdateCsvModel>builder()
            .object(PlatformCompleteUpdateCsvModel.builder()
                .sloid("unknown:sloid")
                .validFrom(LocalDate.of(2023, 1, 1))
                .validTo(LocalDate.of(2023, 6, 30))
                .additionalInformation(ADDITIONAL_INFORMATION)
                .build())
            .build());
    assertThatExceptionOfType(SloidNotFoundException.class).isThrownBy(update);
  }

  @Test
  void shouldThrowIllegalStateExceptionComplete() {
    ThrowingCallable update = () -> platformBulkImportService.updatePlatformComplete(
        BulkImportUpdateContainer.<PlatformCompleteUpdateCsvModel>builder()
            .object(PlatformCompleteUpdateCsvModel.builder()
                .validFrom(LocalDate.of(2023, 1, 1))
                .validTo(LocalDate.of(2023, 6, 30))
                .additionalInformation(ADDITIONAL_INFORMATION)
                .build())
            .build());
    assertThatExceptionOfType(IllegalStateException.class).isThrownBy(update);
  }
}
