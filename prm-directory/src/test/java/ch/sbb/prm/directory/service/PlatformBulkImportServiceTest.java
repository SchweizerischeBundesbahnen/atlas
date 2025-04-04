package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel;
import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel.Fields;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.security.PrmUserAdministrationService;
import ch.sbb.prm.directory.service.bulk.PlatformBulkImportService;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
class PlatformBulkImportServiceTest {

  @MockBean
  private PrmUserAdministrationService prmUserAdministrationService;

  @Autowired
  private PlatformRepository platformRepository;

  @Autowired
  private StopPointRepository stopPointRepository;

  @Autowired
  private PlatformBulkImportService platformBulkImportService;

  private PlatformVersion platformVersion;

  private static final String ADDITIONAL_INFORMATION = "Additional information";

  @BeforeEach
  void setUp() {
    doReturn(true).when(prmUserAdministrationService).hasUserRightsToCreateOrEditPrmObject(any());
    platformVersion = PlatformTestData.getReducedPlatformVersion();
    platformRepository.save(platformVersion);
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
    assertThat(platformVersion.getAdditionalInformation()).isNull();

    platformBulkImportService.updatePlatformReduced(BulkImportUpdateContainer.<PlatformReducedUpdateCsvModel>builder()
        .object(PlatformReducedUpdateCsvModel.builder()
            .sloid(platformVersion.getSloid())
            .validFrom(platformVersion.getValidFrom())
            .validTo(platformVersion.getValidTo())
            .additionalInformation(ADDITIONAL_INFORMATION)
            .build())
        .build());
    PlatformVersion platform =
        platformRepository.findById(platformVersion.getId()).orElseThrow();
    assertThat(platform.getAdditionalInformation()).isEqualTo(ADDITIONAL_INFORMATION);
  }

  @Test
  void shouldUpdateBulkWithUserInNameOf() {
    platformBulkImportService.updatePlatformReducedByUsername("e123456",
        BulkImportUpdateContainer.<PlatformReducedUpdateCsvModel>builder()
            .object(PlatformReducedUpdateCsvModel.builder()
                .sloid(platformVersion.getSloid())
                .validFrom(platformVersion.getValidFrom())
                .validTo(platformVersion.getValidTo())
                .additionalInformation(ADDITIONAL_INFORMATION)
                .build())
            .build());

    PlatformVersion platformVersion1 =
        platformRepository.findById(platformVersion.getId()).orElseThrow();
    assertThat(platformVersion1.getAdditionalInformation()).isEqualTo(ADDITIONAL_INFORMATION);
  }

  @Test
  void shouldUpdateBulkRemovingProperty() {
    assertThat(platformVersion.getHeight()).isEqualTo(123.12);

    platformBulkImportService.updatePlatformReduced(BulkImportUpdateContainer.<PlatformReducedUpdateCsvModel>builder()
        .object(PlatformReducedUpdateCsvModel.builder()
            .sloid(platformVersion.getSloid())
            .validFrom(platformVersion.getValidFrom())
            .validTo(platformVersion.getValidTo())
            .build())
        .attributesToNull(List.of(Fields.height, Fields.additionalInformation, Fields.inclinationLongitudinal))
        .build());

    PlatformVersion platformVersion1 =
        platformRepository.findById(platformVersion.getId()).orElseThrow();
    assertThat(platformVersion1.getHeight()).isNull();
  }

  @Test
  void shouldUpdateAndGetMoreVersions() {
    assertThat(platformRepository.findAllBySloidOrderByValidFrom(platformVersion.getSloid())).hasSize(1);

    platformBulkImportService.updatePlatformReduced(BulkImportUpdateContainer.<PlatformReducedUpdateCsvModel>builder()
        .object(PlatformReducedUpdateCsvModel.builder()
            .sloid(platformVersion.getSloid())
            .validFrom(LocalDate.of(2000, 4, 1))
            .validTo(LocalDate.of(2000, 7, 31))
            .additionalInformation(ADDITIONAL_INFORMATION)
            .build())
        .build());

    List<PlatformVersion> versions =
        platformRepository.findAllBySloidOrderByValidFrom(platformVersion.getSloid());
    assertThat(versions).hasSize(3);

    PlatformVersion firstVersion = versions.getFirst();
    assertThat(firstVersion.getValidFrom()).isEqualTo(LocalDate.of(2000,  1, 1));
    assertThat(firstVersion.getValidTo()).isEqualTo(LocalDate.of(2000,  3, 31));
    assertThat(firstVersion.getAdditionalInformation()).isNull();

    PlatformVersion secondVersion = versions.get(1);
    assertThat(secondVersion.getValidFrom()).isEqualTo(LocalDate.of(2000,  4, 1));
    assertThat(secondVersion.getValidTo()).isEqualTo(LocalDate.of(2000,  7, 31));
    assertThat(secondVersion.getAdditionalInformation()).isEqualTo(ADDITIONAL_INFORMATION);

    PlatformVersion thirdVersion = versions.getLast();
    assertThat(thirdVersion.getValidFrom()).isEqualTo(LocalDate.of(2000,  8, 1));
    assertThat(thirdVersion.getValidTo()).isEqualTo(LocalDate.of(2000,  12, 31));
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

}
