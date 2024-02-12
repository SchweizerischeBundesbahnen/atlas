package ch.sbb.exportservice.integration;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TYPE_JOB_PARAMETER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.exportservice.BatchDataSourceConfigTest;
import ch.sbb.exportservice.model.SePoDiExportType;
import ch.sbb.exportservice.model.ServicePointVersionCsvModel;
import ch.sbb.exportservice.utils.JobDescriptionConstants;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@BatchDataSourceConfigTest
@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
 class ExportCsvDataIntegrationTest extends BaseExportCsvDataIntegrationTest{


  @Test
   void shouldExportDataWithoutSemiColonContent() throws Exception {
    when(amazonService.putZipFile(any(), fileArgumentCaptor.capture(), any())).thenReturn(new URL("https://sbb.ch"));
    when(fileCsvDeletingTasklet.execute(any(), any())).thenReturn(null);

    JobParameters jobParameters = new JobParametersBuilder()
        .addString(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, JobDescriptionConstants.EXECUTION_BATCH_PARAMETER)
        .addString(EXPORT_TYPE_JOB_PARAMETER, SePoDiExportType.WORLD_FULL.toString())
        .addLong(JobDescriptionConstants.START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();

    // when
    jobLauncher.run(exportServicePointCsvJob, jobParameters);

    // then
    File exportedCsvFile = fileArgumentCaptor.getValue();
    List<ServicePointVersionCsvModel> exportedCsv = parseCsv(exportedCsvFile);
    Files.delete(exportedCsvFile.toPath());

    ServicePointVersionCsvModel servicePointVersionCsvModel = exportedCsv.stream().filter(i -> i.getNumber().equals(8572241)).findFirst()
        .orElseThrow();
    assertThat(servicePointVersionCsvModel.getFotComment()).isEqualTo("(Bus): ohne: Fahrplandaten 2016/2018");
  }

  @Test
   void shouldExportDataWithoutNewLine() throws Exception {
    when(amazonService.putZipFile(any(), fileArgumentCaptor.capture(), any())).thenReturn(new URL("https://sbb.ch"));
    when(fileCsvDeletingTasklet.execute(any(), any())).thenReturn(null);

    JobParameters jobParameters = new JobParametersBuilder()
        .addString(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, JobDescriptionConstants.EXECUTION_BATCH_PARAMETER)
        .addString(EXPORT_TYPE_JOB_PARAMETER, SePoDiExportType.WORLD_FULL.toString())
        .addLong(JobDescriptionConstants.START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();

    // when
    jobLauncher.run(exportServicePointCsvJob, jobParameters);

    // then
    File exportedCsvFile = fileArgumentCaptor.getValue();
    List<ServicePointVersionCsvModel> exportedCsv = parseCsv(exportedCsvFile);
    Files.delete(exportedCsvFile.toPath());

    ServicePointVersionCsvModel servicePointVersionCsvModel = exportedCsv.stream().filter(i -> i.getNumber().equals(9411114)).findFirst()
        .orElseThrow();
    assertThat(servicePointVersionCsvModel.getFotComment()).isEqualTo("bern sbb ch");
  }


}
