package ch.sbb.exportservice.integration;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.exportservice.BatchDataSourceConfigTest;
import ch.sbb.exportservice.job.sepodi.servicepoint.ServicePointVersionCsvModel;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@BatchDataSourceConfigTest
@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
class ExportCsvServicePointDataIntegrationTest extends BaseExportCsvDataIntegrationTest {

  @Test
  void shouldExportServicePointToCsvWithCorrectData() throws Exception {
    when(amazonService.putZipFileCleanupBoth(any(), fileArgumentCaptor.capture(), any())).thenReturn(new URL("https://sbb.ch"));
    when(deleteCsvFileTasklet.execute(any(), any())).thenReturn(null);

    // when
    exportServicePointJobService.startExportJobs();

    // then
    File exportedCsvFile =
        fileArgumentCaptor.getAllValues().stream().filter(i -> i.getName().startsWith("full-world-")).findFirst().orElseThrow();
    List<ServicePointVersionCsvModel> exportedCsv = parseCsv(exportedCsvFile);
    Files.delete(exportedCsvFile.toPath());

    ServicePointVersionCsvModel magdenObrist = exportedCsv.stream().filter(i -> i.getNumber().equals(8572241)).findFirst()
        .orElseThrow();
    assertThat(magdenObrist.getBusinessOrganisationNumber()).isEqualTo(999);
    assertThat(magdenObrist.getBusinessOrganisationAbbreviationDe()).isEqualTo("SAS-Code");
    assertThat(magdenObrist.getIsoCountryCode()).isEqualTo("RU");
  }

}
