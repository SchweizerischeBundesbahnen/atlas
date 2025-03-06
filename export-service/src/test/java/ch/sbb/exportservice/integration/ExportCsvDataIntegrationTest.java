package ch.sbb.exportservice.integration;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.exportservice.BatchDataSourceConfigTest;
import ch.sbb.exportservice.model.SePoDiExportType;
import ch.sbb.exportservice.model.ServicePointVersionCsvModel;
import ch.sbb.exportservice.utils.JobDescriptionConstants;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TYPE_JOB_PARAMETER;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@BatchDataSourceConfigTest
@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
class ExportCsvDataIntegrationTest extends BaseExportCsvDataIntegrationTest {

  @Test
  void shouldExportDataWithoutSemiColonContent() throws Exception {
    when(amazonService.putZipFileCleanupBoth(any(), fileArgumentCaptor.capture(), any())).thenReturn(new URL("https://sbb.ch"));
    when(deleteCsvFileTasklet.execute(any(), any())).thenReturn(null);

    // when
    exportServicePointJobService.startExportJobs();

    // then
    File exportedCsvFile =
        fileArgumentCaptor.getAllValues().stream().filter(i -> i.getName().startsWith("full-world-")).findFirst().orElseThrow();

    List<ServicePointVersionCsvModel> exportedCsv = parseCsv(exportedCsvFile);
    Files.delete(exportedCsvFile.toPath());

    ServicePointVersionCsvModel servicePointVersionCsvModel = exportedCsv.stream().filter(i -> i.getNumber().equals(8572241))
        .findFirst()
        .orElseThrow();
    assertThat(servicePointVersionCsvModel.getFotComment()).isEqualTo("(Bus): ohne: Fahrplandaten 2016/2018");
  }

  @Test
  void shouldExportDataWithoutNewLine() throws Exception {
    when(amazonService.putZipFileCleanupBoth(any(), fileArgumentCaptor.capture(), any())).thenReturn(new URL("https://sbb.ch"));
    when(deleteCsvFileTasklet.execute(any(), any())).thenReturn(null);

    // when
    exportServicePointJobService.startExportJobs();

    // then
    File exportedCsvFile =
        fileArgumentCaptor.getAllValues().stream().filter(i -> i.getName().startsWith("full-world-")).findFirst().orElseThrow();

    List<ServicePointVersionCsvModel> exportedCsv = parseCsv(exportedCsvFile);
    Files.delete(exportedCsvFile.toPath());

    ServicePointVersionCsvModel servicePointVersionCsvModel = exportedCsv.stream().filter(i -> i.getNumber().equals(9411114))
        .findFirst()
        .orElseThrow();
    assertThat(servicePointVersionCsvModel.getFotComment()).isEqualTo("bern sbb ch");
  }

}
