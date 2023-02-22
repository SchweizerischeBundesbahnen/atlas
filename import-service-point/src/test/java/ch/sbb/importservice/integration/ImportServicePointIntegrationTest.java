package ch.sbb.importservice.integration;

import static ch.sbb.importservice.controller.ImportServicePointBatchControllerApiV1.IMPORT_LOADING_POINT_CSV_JOB;
import static ch.sbb.importservice.controller.ImportServicePointBatchControllerApiV1.IMPORT_SERVICE_POINT_CSV_JOB;
import static ch.sbb.importservice.service.CsvService.DINSTELLE_FILE_PREFIX;
import static ch.sbb.importservice.service.JobHelperService.MIN_LOCAL_DATE;
import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_BATCH_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_TYPE_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.FULL_PATH_FILENAME_JOB_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_LOADING_POINT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_SERVICE_POINT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.START_AT_JOB_PARAMETER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import ch.sbb.atlas.imports.servicepoint.model.ServicePointItemImportResult;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.ServicePointTestData;
import ch.sbb.importservice.client.SePoDiClient;
import ch.sbb.importservice.service.CsvService;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.MailProducerService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
public class ImportServicePointIntegrationTest {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier(IMPORT_LOADING_POINT_CSV_JOB)
  private Job importLoadingPointCsvJob;

  @Autowired
  @Qualifier(IMPORT_SERVICE_POINT_CSV_JOB)
  private Job importServicePointCsvJob;

  @MockBean
  private CsvService csvService;

  @MockBean
  private SePoDiClient sePoDiClient;

  @MockBean
  private FileHelperService fileHelperService;

  @MockBean
  private MailProducerService mailProducerService;

  @Test
  public void shouldExecuteImportLoadingPointJobDownloadingFileFromS3() throws Exception {
    // given
    List<LoadingPointCsvModel> loadingPointCsvModels = new ArrayList<>();
    when(csvService.getActualLoadingPointCsvModelsFromS3()).thenReturn(loadingPointCsvModels);
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
  public void shouldExecuteImportServicePointJobDownloadingFileFromS3() throws Exception {
    // given
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = ServicePointTestData.getServicePointCsvModelContainers();

    List<ServicePointItemImportResult> servicePointItemImportResults = ServicePointTestData.getServicePointItemImportResults(
        servicePointCsvModelContainers);

    when(csvService.getActualServicePointCsvModelsFromS3()).thenReturn(servicePointCsvModelContainers);
    doNothing().when(mailProducerService).produceMailNotification(any());
    when(sePoDiClient.postServicePointsImport(any())).thenReturn(servicePointItemImportResults);

    JobParameters jobParameters = new JobParametersBuilder()
        .addString(EXECUTION_TYPE_PARAMETER, EXECUTION_BATCH_PARAMETER)
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    // when
    JobExecution jobExecution = jobLauncher.run(importServicePointCsvJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(IMPORT_SERVICE_POINT_CSV_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());

    verify(mailProducerService, times(1)).produceMailNotification(any());
    verify(csvService, times(1)).getActualServicePointCsvModelsFromS3();
  }

  @Test
  public void shouldExecuteImportServicePointJobFromGivenFile() throws Exception {
    // given
    File file = new File(this.getClass().getClassLoader().getResource("DIENSTSTELLEN_V3_IMPORT.csv").getFile());
    when(fileHelperService.downloadImportFileFromS3(DINSTELLE_FILE_PREFIX)).thenReturn(file);
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = ServicePointTestData.getServicePointCsvModelContainers();

    List<ServicePointItemImportResult> servicePointItemImportResults = ServicePointTestData.getServicePointItemImportResults(
        servicePointCsvModelContainers);
    when(csvService.getActualServicePointCsvModels(file)).thenReturn(servicePointCsvModelContainers);
    doCallRealMethod().when(csvService).getActualServicePointCsvModels(file);
    List<ServicePointCsvModel> defaultServicePointCsvModels = ServicePointTestData.getDefaultServicePointCsvModels(123);
    when(csvService.getCsvModelsToUpdate(file, MIN_LOCAL_DATE, ServicePointCsvModel.class)).thenReturn(
        defaultServicePointCsvModels);
    doNothing().when(mailProducerService).produceMailNotification(any());
    when(sePoDiClient.postServicePointsImport(any())).thenReturn(servicePointItemImportResults);

    JobParameters jobParameters = new JobParametersBuilder()
        .addString(FULL_PATH_FILENAME_JOB_PARAMETER, file.getAbsolutePath())
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    // when
    JobExecution jobExecution = jobLauncher.run(importServicePointCsvJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(IMPORT_SERVICE_POINT_CSV_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());

  }

}


