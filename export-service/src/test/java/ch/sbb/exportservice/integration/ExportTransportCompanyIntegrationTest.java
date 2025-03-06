package ch.sbb.exportservice.integration;

import ch.sbb.atlas.export.CsvExportWriter;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.exportservice.BoDiDbSchemaCreation;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.service.BaseExportJobService;
import ch.sbb.exportservice.service.BaseExportJobService.JobParams;
import ch.sbb.exportservice.tasklet.delete.DeleteCsvFileTaskletV2;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRANSPORT_COMPANY_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRANSPORT_COMPANY_JSON_JOB_NAME;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@BoDiDbSchemaCreation
@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
class ExportTransportCompanyIntegrationTest extends BaseExportCsvDataIntegrationTest {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier(EXPORT_TRANSPORT_COMPANY_CSV_JOB_NAME)
  private Job exportTransportCompanyCsvJob;

  @Autowired
  @Qualifier(EXPORT_TRANSPORT_COMPANY_JSON_JOB_NAME)
  private Job exportTransportCompanyJsonJob;

  @MockitoBean
  @Qualifier("deleteTransportCompanyCsvFileTasklet")
  private DeleteCsvFileTaskletV2 transportCompanyCsvFileDeletingTasklet;

  @Test
  void shouldExecuteExportTransportCompanyCsvJob() throws Exception {
    when(amazonService.putZipFileCleanupZip(any(), fileArgumentCaptor.capture(), any())).thenReturn(
        URI.create("https://sbb.ch").toURL());
    when(transportCompanyCsvFileDeletingTasklet.execute(any(), any())).thenReturn(null);

    // given
    JobParameters jobParameters = BaseExportJobService.buildJobParameters(new JobParams(ExportTypeV2.FULL));
    // when
    JobExecution jobExecution = jobLauncher.run(exportTransportCompanyCsvJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(EXPORT_TRANSPORT_COMPANY_CSV_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());

    File exportedCsvFile = fileArgumentCaptor.getValue();
    String fileContent = Files.readString(exportedCsvFile.toPath());
    Files.delete(exportedCsvFile.toPath());

    assertThat(fileContent).isEqualToIgnoringNewLines(CsvExportWriter.UTF_8_BYTE_ORDER_MARK + """
        id;number;abbreviation;description;businessRegisterName;transportCompanyStatus;businessRegisterNumber;enterpriseId;ricsCode;businessOrganisationNumbers;comment;creationDate;editionDate
        2893;#20001;#ALCOSUI;;Alcosuisse;OPERATING_PART;;CHE-100.966.104;;;;2022-08-04 16:13:51;2022-08-23 01:00:14
        2895;#20005;COOP-Aclens;;Coop Société coopérative, Aclens;OPERATING_PART;;CHE-302.816.540;;;;2022-08-04 16:13:51;2022-08-23 01:00:14
        2896;#20006;#HOLCIWL;;Holcim (Schweiz) AG, Würenlingen (Werk Siggenthal);OPERATING_PART;;CHE-105.953.103;;;;2022-08-04 16:13:51;2022-08-23 01:00:14
        """);
  }

  @Test
  void shouldExecuteExportTransportCompanyJsonJob() throws Exception {
    // given
    JobParameters jobParameters = BaseExportJobService.buildJobParameters(new JobParams(ExportTypeV2.FULL));
    // when
    JobExecution jobExecution = jobLauncher.run(exportTransportCompanyJsonJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(EXPORT_TRANSPORT_COMPANY_JSON_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

}
