package ch.sbb.importservice.integration;

import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.client.SePoDiClient;
import ch.sbb.importservice.service.MailProducerService;
import ch.sbb.importservice.service.csv.LoadingPointCsvService;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_BATCH_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_TYPE_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.FULL_PATH_FILENAME_JOB_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_LOADING_POINT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.START_AT_JOB_PARAMETER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
 class ImportLoadingPointIntegrationTest {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier(IMPORT_LOADING_POINT_CSV_JOB_NAME)
  private Job importLoadingPointCsvJob;

  @MockBean
  private LoadingPointCsvService loadingPointService;

  @MockBean
  private SePoDiClient sePoDiClient;

  @MockBean
  private MailProducerService mailProducerService;

  @Test
   void shouldExecuteImportLoadingPointJobDownloadingFileFromS3() throws Exception {
    // given
    List<LoadingPointCsvModel> loadingPointCsvModels = new ArrayList<>();
    when(loadingPointService.getActualCsvModelsFromS3()).thenReturn(loadingPointCsvModels);
    doNothing().when(mailProducerService).produceMailNotification(any());

    JobParameters jobParameters = new JobParametersBuilder()
        .addString(EXECUTION_TYPE_PARAMETER, EXECUTION_BATCH_PARAMETER)
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    // when
    JobExecution jobExecution = jobLauncher.run(importLoadingPointCsvJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(IMPORT_LOADING_POINT_CSV_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

  @Test
  void shouldExecuteImportLoadingPointJobFromGivenFile()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException,
      JobRestartException {
    // given
    File file = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("LADESTELLEN_V3_IMPORT.csv")).getFile());

    when(sePoDiClient.postLoadingPointsImport(any())).thenReturn(List.of());
    doNothing().when(mailProducerService).produceMailNotification(any());

    JobParameters jobParameters = new JobParametersBuilder()
        .addString(FULL_PATH_FILENAME_JOB_PARAMETER, file.getAbsolutePath())
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();

    // when
    JobExecution jobExecution = jobLauncher.run(importLoadingPointCsvJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(IMPORT_LOADING_POINT_CSV_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

}
