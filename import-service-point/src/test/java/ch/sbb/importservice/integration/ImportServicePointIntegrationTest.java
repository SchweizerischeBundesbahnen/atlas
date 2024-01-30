package ch.sbb.importservice.integration;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.ServicePointTestData;
import ch.sbb.importservice.client.SePoDiClient;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.MailProducerService;
import ch.sbb.importservice.service.csv.CsvFileNameModel;
import ch.sbb.importservice.service.csv.ServicePointCsvService;
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

import java.io.File;
import java.util.List;
import java.util.Objects;

import static ch.sbb.importservice.service.JobHelperService.MIN_LOCAL_DATE;
import static ch.sbb.importservice.service.csv.CsvFileNameModel.SERVICEPOINT_DIDOK_DIR_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_BATCH_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_TYPE_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.FULL_PATH_FILENAME_JOB_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_SERVICE_POINT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.START_AT_JOB_PARAMETER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
 class ImportServicePointIntegrationTest {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier(IMPORT_SERVICE_POINT_CSV_JOB_NAME)
  private Job importServicePointCsvJob;

  @MockBean
  private ServicePointCsvService servicePointCsvService;

  @MockBean
  private SePoDiClient sePoDiClient;

  @MockBean
  private FileHelperService fileHelperService;

  @MockBean
  private MailProducerService mailProducerService;

  private final CsvFileNameModel csvFileNameModel = CsvFileNameModel.builder()
     .fileName(ServicePointCsvService.SERVICE_POINT_FILE_PREFIX)
     .s3BucketDir(SERVICEPOINT_DIDOK_DIR_NAME)
     .addDateToPostfix(true)
     .build();

  @Test
   void shouldExecuteImportServicePointJobDownloadingFileFromS3() throws Exception {
    // given
    List<ServicePointCsvModel> servicePointCsvModels = ServicePointTestData
        .getDefaultServicePointCsvModels(85070005);

    List<ItemImportResult> itemImportResults = ServicePointTestData.getServicePointItemImportResults(
        ServicePointTestData.getServicePointCsvModelContainers());

    when(servicePointCsvService.getActualCsvModelsFromS3()).thenReturn(servicePointCsvModels);
    doNothing().when(mailProducerService).produceMailNotification(any());
    when(sePoDiClient.postServicePointsImport(any())).thenReturn(itemImportResults);

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
    verify(servicePointCsvService, times(1)).getActualCsvModelsFromS3();
  }

  @Test
   void shouldExecuteImportServicePointJobFromGivenFile() throws Exception {
    // given

    File file = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("DIENSTSTELLEN_V3_IMPORT.csv")).getFile());
    when(fileHelperService.downloadImportFileFromS3(csvFileNameModel)).thenReturn(file);
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = ServicePointTestData
        .getServicePointCsvModelContainers();

    List<ItemImportResult> itemImportResults = ServicePointTestData.getServicePointItemImportResults(
        servicePointCsvModelContainers);
    when(servicePointCsvService.getActualCsvModels(file)).thenReturn(
        ServicePointTestData.getDefaultServicePointCsvModels(85070005));
    doCallRealMethod().when(servicePointCsvService).getActualCsvModels(file);
    List<ServicePointCsvModel> defaultServicePointCsvModels = ServicePointTestData
        .getDefaultServicePointCsvModels(123);
    when(servicePointCsvService.getCsvModelsToUpdate(file, MIN_LOCAL_DATE)).thenReturn(
        defaultServicePointCsvModels);
    doNothing().when(mailProducerService).produceMailNotification(any());
    when(sePoDiClient.postServicePointsImport(any())).thenReturn(itemImportResults);

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
