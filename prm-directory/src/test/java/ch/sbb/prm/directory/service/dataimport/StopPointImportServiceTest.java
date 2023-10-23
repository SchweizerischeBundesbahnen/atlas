package ch.sbb.prm.directory.service.dataimport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModel;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.testdata.prm.StopPointCsvTestData;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.repository.StopPointRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class StopPointImportServiceTest {

  private final StopPointRepository stopPointRepository;

  @MockBean
  private final VersionableService versionableService;

  private final StopPointImportService stopPointImportService;

  @Autowired
  StopPointImportServiceTest(StopPointRepository stopPointRepository, VersionableService versionableService,
      StopPointImportService stopPointImportService) {
    this.stopPointRepository = stopPointRepository;
    this.versionableService = versionableService;
    this.stopPointImportService = stopPointImportService;
  }

  @Test
  void shouldImportWhenStopPointsDoesNotExists() {
    //when
    List<ItemImportResult> result = stopPointImportService.importServicePoints(
        List.of(StopPointCsvTestData.getStopPointCsvModelContainer()));

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getMessage()).isEqualTo("[SUCCESS]: This version was imported successfully");
    assertThat(result.get(0).getItemNumber()).isEqualTo("1234567");
    assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2000, 12, 31));
    assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.SUCCESS);
    verify(versionableService, never()).versioningObjectsDeletingNullProperties(any(), any(), any());
  }

  @Test
  void shouldImportWhenStopPointsDoesNotHaveValidFromMandatoryField() {
    StopPointCsvModelContainer stopPointCsvModelContainer = StopPointCsvTestData.getStopPointCsvModelContainer();
    List<StopPointCsvModel> stopPointCsvModels = stopPointCsvModelContainer.getStopPointCsvModels();
    stopPointCsvModels.get(0).setValidFrom(null);
    //when
    List<ItemImportResult> result = stopPointImportService.importServicePoints(
        List.of(stopPointCsvModelContainer));

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getItemNumber()).isEqualTo("1234567");
    assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.FAILED);
    verify(versionableService, never()).versioningObjectsDeletingNullProperties(any(), any(), any());
  }

  @Test
  void shouldImportWhenMeansOfTransportFieldIsNull() {
    StopPointCsvModelContainer stopPointCsvModelContainer = StopPointCsvTestData.getStopPointCsvModelContainer();
    List<StopPointCsvModel> stopPointCsvModels = stopPointCsvModelContainer.getStopPointCsvModels();
    stopPointCsvModels.get(0).setTransportationMeans(null);
    //when
    List<ItemImportResult> result = stopPointImportService.importServicePoints(
        List.of(stopPointCsvModelContainer));

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getItemNumber()).isEqualTo("1234567");
    assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.FAILED);
    verify(versionableService, never()).versioningObjectsDeletingNullProperties(any(), any(), any());
  }

  @Test
  void shouldImportWhenStopPointsExists() {
    //then
    StopPointCsvModelContainer stopPointCsvModelContainer = StopPointCsvTestData.getStopPointCsvModelContainer();
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    Integer didokCode = stopPointCsvModelContainer.getDidokCode();
    stopPointVersion.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(didokCode));
    stopPointRepository.saveAndFlush(stopPointVersion);

    //when
    List<ItemImportResult> result = stopPointImportService.importServicePoints(List.of(stopPointCsvModelContainer));

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getMessage()).isEqualTo("[SUCCESS]: This version was imported successfully");
    assertThat(result.get(0).getItemNumber()).isEqualTo("1234567");
    assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2000, 12, 31));
    assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.SUCCESS);
    verify(versionableService).versioningObjectsDeletingNullProperties(any(), any(), any());
  }

}