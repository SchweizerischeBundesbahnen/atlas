package ch.sbb.importservice.integration;

import static ch.sbb.importservice.contoller.ImportController.IMPORT_LOADING_POINT_CSV_JOB;
import static ch.sbb.importservice.contoller.ImportController.IMPORT_SERVICE_POINT_CSV_JOB;
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

import ch.sbb.atlas.base.service.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import ch.sbb.atlas.base.service.imports.servicepoint.model.ServicePointImportReqModel;
import ch.sbb.atlas.base.service.imports.servicepoint.model.ServicePointItemImportResult;
import ch.sbb.atlas.base.service.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.base.service.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.importservice.client.SePoDiClient;
import ch.sbb.importservice.service.CsvService;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.MailProducerService;
import java.io.File;
import java.time.LocalDate;
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
import org.springframework.kafka.test.context.EmbeddedKafka;

@EmbeddedKafka(topics = {"atlas.mail", "atlas.workflow"})
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
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
  }

  @Test
  public void shouldExecuteImportServicePointJobDownloadingFileFromS3() throws Exception {
    // given
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = getServicePointCsvModelContainers();

    List<ServicePointItemImportResult> servicePointItemImportResults = getServicePointItemImportResults(
        servicePointCsvModelContainers);

    when(csvService.getActualServicePotinCsvModelsFromS3()).thenReturn(servicePointCsvModelContainers);
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
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");

    verify(mailProducerService, times(1)).produceMailNotification(any());
    verify(csvService, times(1)).getActualServicePotinCsvModelsFromS3();
  }

  @Test
  public void shouldExecuteImportServicePointJobFromGivenFile() throws Exception {
    // given
    File file = new File(this.getClass().getClassLoader().getResource("import.csv").getFile());
    when(fileHelperService.downloadImportFileFromS3(DINSTELLE_FILE_PREFIX)).thenReturn(file);
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = getServicePointCsvModelContainers();

    List<ServicePointItemImportResult> servicePointItemImportResults = getServicePointItemImportResults(
        servicePointCsvModelContainers);
    when(csvService.getActualServicePotinCsvModelsFromS3(file)).thenReturn(servicePointCsvModelContainers);
    doCallRealMethod().when(csvService).getActualServicePotinCsvModelsFromS3(file);
    List<ServicePointCsvModel> defaultServicePointCsvModels = getDefaultServicePointCsvModels(123);
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
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
  }

  private List<ServicePointItemImportResult> getServicePointItemImportResults(
      List<ServicePointCsvModelContainer> servicePointCsvModelContainers) {
    ServicePointImportReqModel servicePointImportReqModel = new ServicePointImportReqModel();
    servicePointImportReqModel.setServicePointCsvModelContainers(servicePointCsvModelContainers);

    List<ServicePointItemImportResult> servicePointItemImportResults = new ArrayList<>();
    for (ServicePointCsvModelContainer container : servicePointCsvModelContainers) {

      ServicePointItemImportResult servicePointItemImportResult = new ServicePointItemImportResult();
      servicePointItemImportResult.setItemNumber(container.getDidokCode());
      servicePointItemImportResult.setStatus("COMPLETE");
      servicePointItemImportResults.add(servicePointItemImportResult);
    }
    return servicePointItemImportResults;
  }

  private List<ServicePointCsvModelContainer> getServicePointCsvModelContainers() {
    ServicePointCsvModelContainer servicePointCsvModelContainer1 = getServicePointCsvModelContainer(123);
    ServicePointCsvModelContainer servicePointCsvModelContainer2 = getServicePointCsvModelContainer(124);

    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = new ArrayList<>();
    servicePointCsvModelContainers.add(servicePointCsvModelContainer1);
    servicePointCsvModelContainers.add(servicePointCsvModelContainer2);
    return servicePointCsvModelContainers;
  }

  private ServicePointCsvModelContainer getServicePointCsvModelContainer(Integer didokNumber) {
    List<ServicePointCsvModel> csvModelsToUpdate = getDefaultServicePointCsvModels(
        didokNumber);
    ServicePointCsvModelContainer servicePointCsvModelContainer = new ServicePointCsvModelContainer();
    servicePointCsvModelContainer.setDidokCode(didokNumber);
    servicePointCsvModelContainer.setServicePointCsvModelList(csvModelsToUpdate);
    return servicePointCsvModelContainer;
  }

  private List<ServicePointCsvModel> getDefaultServicePointCsvModels(Integer didokNumber) {
    ServicePointCsvModel servicePointCsvModel1 = getServicePointModel(didokNumber, LocalDate.now(), LocalDate.now());
    ServicePointCsvModel servicePointCsvModel2 = getServicePointModel(didokNumber, LocalDate.now().plusMonths(1),
        LocalDate.now().plusMonths(1));
    List<ServicePointCsvModel> csvModelsToUpdate = new ArrayList<>();
    csvModelsToUpdate.add(servicePointCsvModel1);
    csvModelsToUpdate.add(servicePointCsvModel2);
    return csvModelsToUpdate;
  }

  private ServicePointCsvModel getServicePointModel(Integer didokNumber, LocalDate validFrom, LocalDate validTo) {
    ServicePointCsvModel servicePointCsvModel = new ServicePointCsvModel();
    servicePointCsvModel.setIsVirtuell(true);
    servicePointCsvModel.setValidFrom(validFrom);
    servicePointCsvModel.setValidTo(validTo);
    servicePointCsvModel.setNummer(didokNumber);
    servicePointCsvModel.setDidokCode(didokNumber);
    return servicePointCsvModel;
  }

}


