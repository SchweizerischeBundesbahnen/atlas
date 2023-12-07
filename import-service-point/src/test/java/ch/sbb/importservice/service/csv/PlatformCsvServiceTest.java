package ch.sbb.importservice.service.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.imports.prm.platform.PlatformCsvModel;
import ch.sbb.atlas.imports.prm.platform.PlatformCsvModelContainer;
import ch.sbb.atlas.testdata.prm.PlatformCsvTestData;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class PlatformCsvServiceTest {

  private PlatformCsvService platformCsvService;

  @Mock
  private FileHelperService fileHelperService;

  @Mock
  private JobHelperService jobHelperService;

  @BeforeEach
  void setUp() {
    openMocks(this);
    platformCsvService = new PlatformCsvService(fileHelperService, jobHelperService);
  }

  @Test
  void shouldHaveCorrectFileName() {
    CsvFileNameModel csvFileName = platformCsvService.csvFileNameModel();
    assertThat(csvFileName.getFileName()).startsWith("PRM_PLATFORMS");
  }

  @Test
  void shouldMergeSequentialEqualsPlatforms() {
    // given
    PlatformCsvModel platformCsvModel1 = PlatformCsvTestData.getCsvModel();
    PlatformCsvModel platformCsvModel2 = PlatformCsvTestData.getCsvModel();
    platformCsvModel2.setValidFrom(LocalDate.of(2001, 1, 1));
    platformCsvModel2.setValidTo(LocalDate.of(2001, 12, 31));
    List<PlatformCsvModel> csvModels = List.of(platformCsvModel1, platformCsvModel2);

    // when
    List<PlatformCsvModelContainer> result = platformCsvService.mapToPlatformCsvModelContainers(csvModels);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getCsvModels()).hasSize(1);
    assertThat(result.get(0).getCsvModels().get(0).getValidFrom()).isEqualTo(platformCsvModel1.getValidFrom());
    assertThat(result.get(0).getCsvModels().get(0).getValidTo()).isEqualTo(platformCsvModel2.getValidTo());
  }

  @Test
  void shouldMergeEqualsPlatform() {
    // given
    PlatformCsvModel platformCsvModel1 = PlatformCsvTestData.getCsvModel();
    PlatformCsvModel platformCsvModel2 = PlatformCsvTestData.getCsvModel();
    List<PlatformCsvModel> csvModels = List.of(platformCsvModel1, platformCsvModel2);

    // when
    List<PlatformCsvModelContainer> result = platformCsvService.mapToPlatformCsvModelContainers(
        csvModels);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getCsvModels()).hasSize(1);
    assertThat(result.get(0).getCsvModels().get(0).getValidFrom()).isEqualTo(platformCsvModel1.getValidFrom());
    assertThat(result.get(0).getCsvModels().get(0).getValidTo()).isEqualTo(platformCsvModel2.getValidTo());
  }

  @Test
  void shouldReturnOnlyActiveStopPlaces() {
    // given
    PlatformCsvModel platformCsvModel1 = PlatformCsvTestData.getCsvModel();
    platformCsvModel1.setStatus(1);
    PlatformCsvModel platformCsvModel2 = PlatformCsvTestData.getCsvModel();
    platformCsvModel2.setValidFrom(platformCsvModel1.getValidTo().plusDays(1));
    platformCsvModel2.setValidTo(platformCsvModel1.getValidTo().plusYears(1));
    platformCsvModel2.setStatus(0);
    List<PlatformCsvModel> csvModels = List.of(platformCsvModel1,platformCsvModel2);

    // when
    List<PlatformCsvModelContainer> result = platformCsvService.mapToPlatformCsvModelContainers(
        csvModels);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getCsvModels()).hasSize(1);
    assertThat(result.get(0).getCsvModels().get(0).getStatus()).isEqualTo(1);
  }

}
