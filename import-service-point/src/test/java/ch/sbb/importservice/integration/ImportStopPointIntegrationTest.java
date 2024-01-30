package ch.sbb.importservice.integration;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.testdata.prm.StopPointCsvTestData;
import ch.sbb.importservice.client.PrmClient;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.MailProducerService;
import ch.sbb.importservice.service.csv.CsvFileNameModel;
import ch.sbb.importservice.service.csv.StopPointCsvService;
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
import static ch.sbb.importservice.service.csv.CsvFileNameModel.PRM_DIR_NAME;
import static ch.sbb.importservice.service.csv.StopPointCsvService.PRM_STOP_PLACES_FILE_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_BATCH_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_TYPE_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.FULL_PATH_FILENAME_JOB_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_STOP_POINT_CSV_JOB_NAME;
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
class ImportStopPointIntegrationTest {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier(IMPORT_STOP_POINT_CSV_JOB_NAME)
  private Job importStopPointCsvJob;

  @MockBean
  private StopPointCsvService stopPointCsvService;

  @MockBean
  private PrmClient prmClient;

  @MockBean
  private FileHelperService fileHelperService;

  @MockBean
  private MailProducerService mailProducerService;

  private final CsvFileNameModel csvFileNameModel = CsvFileNameModel.builder()
      .fileName(PRM_STOP_PLACES_FILE_NAME)
      .s3BucketDir(PRM_DIR_NAME)
      .addDateToPostfix(true)
      .build();

  @Test
  void shouldExecuteImportStopPointJobDownloadingFileFromS3() throws Exception {
    // given
    List<StopPointCsvModel> stopPointCsvModels = StopPointCsvTestData.getStopPointCsvModels();

    List<ItemImportResult> itemImportResults =
        StopPointCsvTestData.getStopPointItemImportResults(List.of(StopPointCsvTestData.getStopPointCsvModelContainer()));

    when(stopPointCsvService.getActualCsvModelsFromS3()).thenReturn(stopPointCsvModels);
    doNothing().when(mailProducerService).produceMailNotification(any());
    when(prmClient.postStopPointImport(any())).thenReturn(itemImportResults);

    JobParameters jobParameters = new JobParametersBuilder()
        .addString(EXECUTION_TYPE_PARAMETER, EXECUTION_BATCH_PARAMETER)
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    // when
    JobExecution jobExecution = jobLauncher.run(importStopPointCsvJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(IMPORT_STOP_POINT_CSV_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());

    verify(mailProducerService, times(1)).produceMailNotification(any());
    verify(stopPointCsvService, times(1)).getActualCsvModelsFromS3();
  }

  @Test
  void shouldExecuteImportStopPointJobFromGivenFile() throws Exception {
    // given

    File file = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("PRM_STOP_PLACES.csv")).getFile());
    when(fileHelperService.downloadImportFileFromS3(csvFileNameModel)).thenReturn(file);

    List<ItemImportResult> itemImportResults =
        StopPointCsvTestData.getStopPointItemImportResults(List.of(StopPointCsvTestData.getStopPointCsvModelContainer()));
    when(stopPointCsvService.getActualCsvModels(file)).thenReturn(StopPointCsvTestData.getStopPointCsvModels());
    doCallRealMethod().when(stopPointCsvService).getActualCsvModels(file);
    List<StopPointCsvModel> defaultStopPointCsvModels = StopPointCsvTestData.getStopPointCsvModels();
    when(stopPointCsvService.getCsvModelsToUpdate(file, MIN_LOCAL_DATE)).thenReturn(
        defaultStopPointCsvModels);
    doNothing().when(mailProducerService).produceMailNotification(any());
    when(prmClient.postStopPointImport(any())).thenReturn(itemImportResults);

    JobParameters jobParameters = new JobParametersBuilder()
        .addString(FULL_PATH_FILENAME_JOB_PARAMETER, file.getAbsolutePath())
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    // when
    JobExecution jobExecution = jobLauncher.run(importStopPointCsvJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(IMPORT_STOP_POINT_CSV_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

}
