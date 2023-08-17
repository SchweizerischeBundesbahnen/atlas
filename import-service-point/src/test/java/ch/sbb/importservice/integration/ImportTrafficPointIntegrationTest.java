package ch.sbb.importservice.integration;

import static ch.sbb.importservice.service.CsvService.VERKEHRSPUNKTELEMENTE_FILE_PREFIX;
import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_BATCH_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_TYPE_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.FULL_PATH_FILENAME_JOB_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_TRAFFIC_POINT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.START_AT_JOB_PARAMETER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.client.SePoDiClient;
import ch.sbb.importservice.service.CsvService;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.MailProducerService;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
public class ImportTrafficPointIntegrationTest {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier(IMPORT_TRAFFIC_POINT_CSV_JOB_NAME)
  private Job importTrafficPointCsvJob;

  @MockBean
  private CsvService csvService;

  @MockBean
  private SePoDiClient sePoDiClient;

  @MockBean
  private FileHelperService fileHelperService;

  @MockBean
  private MailProducerService mailProducerService;

  @Test
  void shouldExecuteImportTrafficPointJobFromGivenFile()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException,
      JobRestartException {
    // given
    File file = new File(this.getClass().getClassLoader().getResource("VERKEHRSPUNKTELEMENTE_IMPORT.csv").getFile());

    doNothing().when(mailProducerService).produceMailNotification(any());
    when(sePoDiClient.postTrafficPointsImport(any())).thenReturn(List.of());

    JobParameters jobParameters = new JobParametersBuilder()
        .addString(FULL_PATH_FILENAME_JOB_PARAMETER, file.getAbsolutePath())
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();

    // when
    JobExecution jobExecution = jobLauncher.run(importTrafficPointCsvJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(IMPORT_TRAFFIC_POINT_CSV_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

  @Test
  void shouldExecuteImportTrafficPointJobDownloadingFileFromS3()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException,
      JobRestartException {
    // given
    File file = new File(this.getClass().getClassLoader().getResource("VERKEHRSPUNKTELEMENTE_IMPORT.csv").getFile());
    when(fileHelperService.downloadImportFileFromS3(VERKEHRSPUNKTELEMENTE_FILE_PREFIX)).thenReturn(file);
    doNothing().when(mailProducerService).produceMailNotification(any());
    when(sePoDiClient.postTrafficPointsImport(any())).thenReturn(List.of());

    JobParameters jobParameters = new JobParametersBuilder()
        .addString(EXECUTION_TYPE_PARAMETER, EXECUTION_BATCH_PARAMETER)
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();

    // when
    JobExecution jobExecution = jobLauncher.run(importTrafficPointCsvJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(IMPORT_TRAFFIC_POINT_CSV_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());

    verify(mailProducerService, times(1)).produceMailNotification(any());
    verify(csvService, times(1)).getActualTrafficPointCsvModelsFromS3();
  }

}
