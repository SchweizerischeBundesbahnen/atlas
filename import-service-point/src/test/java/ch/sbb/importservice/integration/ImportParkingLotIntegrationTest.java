package ch.sbb.importservice.integration;

import static ch.sbb.importservice.service.JobHelperService.MIN_LOCAL_DATE;
import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_BATCH_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_TYPE_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.FULL_PATH_FILENAME_JOB_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_PARKING_LOT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.START_AT_JOB_PARAMETER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotCsvModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.testdata.prm.ParkingLotCsvTestData;
import ch.sbb.importservice.client.PrmClient;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.MailProducerService;
import ch.sbb.importservice.service.csv.ParkingLotCsvService;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
class ImportParkingLotIntegrationTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier(IMPORT_PARKING_LOT_CSV_JOB_NAME)
    private Job importParkingLotCsvJob;

    @MockBean
    private ParkingLotCsvService parkingLotCsvService;

    @MockBean
    private PrmClient prmClient;

    @MockBean
    private FileHelperService fileHelperService;

    @MockBean
    private MailProducerService mailProducerService;

    @Test
    void shouldExecuteImportParkingLotJobDownloadingFileFromS3() throws Exception {
        // given
        List<ParkingLotCsvModel> parkingLotCsvModels = List.of(ParkingLotCsvTestData.getCsvModel());
        when(parkingLotCsvService.getActualCsvModelsFromS3()).thenReturn(parkingLotCsvModels);

        doNothing().when(mailProducerService).produceMailNotification(any());
        when(prmClient.importParkingLots(any())).thenReturn(Collections.emptyList());

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(EXECUTION_TYPE_PARAMETER, EXECUTION_BATCH_PARAMETER)
                .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
        // when
        JobExecution jobExecution = jobLauncher.run(importParkingLotCsvJob, jobParameters);
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        // then
        assertThat(actualJobInstance.getJobName()).isEqualTo(IMPORT_PARKING_LOT_CSV_JOB_NAME);
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());

        verify(mailProducerService, times(1)).produceMailNotification(any());
        verify(parkingLotCsvService, times(1)).getActualCsvModelsFromS3();
    }

    @Test
    void shouldExecuteImportParkingLotJobFromGivenFile() throws Exception {
        // given
        File file =
            new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("PRM_PARKING_LOTS.csv")).getFile());
        when(fileHelperService.downloadImportFileFromS3(any())).thenReturn(file);

        doCallRealMethod().when(parkingLotCsvService).getActualCsvModels(file);
        when(parkingLotCsvService.getActualCsvModels(file)).thenReturn(List.of(ParkingLotCsvTestData.getCsvModel()));
        when(parkingLotCsvService.getCsvModelsToUpdate(file, MIN_LOCAL_DATE)).thenReturn(List.of(ParkingLotCsvTestData.getCsvModel()));

        when(prmClient.importParkingLots(any())).thenReturn(Collections.emptyList());
        doNothing().when(mailProducerService).produceMailNotification(any());

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(FULL_PATH_FILENAME_JOB_PARAMETER, file.getAbsolutePath())
                .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
        // when
        JobExecution jobExecution = jobLauncher.run(importParkingLotCsvJob, jobParameters);
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        // then
        assertThat(actualJobInstance.getJobName()).isEqualTo(IMPORT_PARKING_LOT_CSV_JOB_NAME);
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
    }

}
