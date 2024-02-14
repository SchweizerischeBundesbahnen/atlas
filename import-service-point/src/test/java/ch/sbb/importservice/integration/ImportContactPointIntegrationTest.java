package ch.sbb.importservice.integration;

import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.testdata.prm.ContactPointCsvTestData;
import ch.sbb.importservice.client.PrmClient;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.MailProducerService;
import ch.sbb.importservice.service.csv.ContactPointCsvService;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import static ch.sbb.importservice.utils.JobDescriptionConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ch.sbb.importservice.service.JobHelperService.MIN_LOCAL_DATE;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
public class ImportContactPointIntegrationTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier(IMPORT_INFO_DESK_CSV_JOB_NAME)
    private Job importInfoDeskCsvJob;

    @Autowired
    @Qualifier(IMPORT_TICKET_COUNTER_CSV_JOB_NAME)
    private Job importTicketCounterCsvJob;

    @MockBean
    private ContactPointCsvService contactPointCsvService;

    @MockBean
    private PrmClient prmClient;

    @MockBean
    private FileHelperService fileHelperService;

    @MockBean
    private MailProducerService mailProducerService;

    @Test
    void shouldExecuteImportInfoDesksJobDownloadingFileFromS3() throws Exception {
        // given
        List<ContactPointCsvModel> contactPointCsvModels = List.of(ContactPointCsvTestData.getCsvModel());
        when(contactPointCsvService.loadFileFromS3("PRM_INFO_DESKS", IMPORT_INFO_DESK_CSV_JOB_NAME, ContactPointType.INFORMATION_DESK)).thenReturn(contactPointCsvModels);

        doNothing().when(mailProducerService).produceMailNotification(any());
        when(prmClient.importContactPoints(any())).thenReturn(Collections.emptyList());

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(EXECUTION_TYPE_PARAMETER, EXECUTION_BATCH_PARAMETER)
                .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
        // when
        JobExecution jobExecution = jobLauncher.run(importInfoDeskCsvJob, jobParameters);
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        // then
        assertThat(actualJobInstance.getJobName()).isEqualTo(IMPORT_INFO_DESK_CSV_JOB_NAME);
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());

        verify(mailProducerService, times(1)).produceMailNotification(any());
        verify(contactPointCsvService, times(1)).loadFileFromS3("PRM_INFO_DESKS", IMPORT_INFO_DESK_CSV_JOB_NAME, ContactPointType.INFORMATION_DESK);
    }

    @Test
    void shouldExecuteImportTicketCountersJobDownloadingFileFromS3() throws Exception {
        // given
        List<ContactPointCsvModel> contactPointCsvModels = List.of(ContactPointCsvTestData.getCsvModel());
        when(contactPointCsvService.loadFileFromS3("PRM_TICKET_COUNTERS", IMPORT_TICKET_COUNTER_CSV_JOB_NAME, ContactPointType.TICKET_COUNTER)).thenReturn(contactPointCsvModels);

        doNothing().when(mailProducerService).produceMailNotification(any());
        when(prmClient.importContactPoints(any())).thenReturn(Collections.emptyList());

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(EXECUTION_TYPE_PARAMETER, EXECUTION_BATCH_PARAMETER)
                .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
        // when
        JobExecution jobExecution = jobLauncher.run(importTicketCounterCsvJob, jobParameters);
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        // then
        assertThat(actualJobInstance.getJobName()).isEqualTo(IMPORT_TICKET_COUNTER_CSV_JOB_NAME);
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());

        verify(mailProducerService, times(1)).produceMailNotification(any());
        verify(contactPointCsvService, times(1)).loadFileFromS3("PRM_TICKET_COUNTERS", IMPORT_TICKET_COUNTER_CSV_JOB_NAME, ContactPointType.TICKET_COUNTER);
    }

    @Test
    void shouldExecuteImportInfoDeskJobFromGivenFile() throws Exception {
        // given
        File file =
                new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("PRM_INFO_DESKS.csv")).getFile());
        when(fileHelperService.downloadImportFileFromS3(any())).thenReturn(file);

        doCallRealMethod().when(contactPointCsvService).getActualCsvModels(file);
        when(contactPointCsvService.getActualCsvModels(file)).thenReturn(List.of(ContactPointCsvTestData.getCsvModel()));
        when(contactPointCsvService.getCsvModelsToUpdate(file, MIN_LOCAL_DATE)).thenReturn(List.of(ContactPointCsvTestData.getCsvModel()));

        when(prmClient.importContactPoints(any())).thenReturn(Collections.emptyList());
        doNothing().when(mailProducerService).produceMailNotification(any());

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(FULL_PATH_FILENAME_JOB_PARAMETER, file.getAbsolutePath())
                .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
        // when
        JobExecution jobExecution = jobLauncher.run(importInfoDeskCsvJob, jobParameters);
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        // then
        assertThat(actualJobInstance.getJobName()).isEqualTo(IMPORT_INFO_DESK_CSV_JOB_NAME);
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
    }

    @Test
    void shouldExecuteImportTicketCounterJobFromGivenFile() throws Exception {
        // given
        File file =
                new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("PRM_TICKET_COUNTERS.csv")).getFile());
        when(fileHelperService.downloadImportFileFromS3(any())).thenReturn(file);

        doCallRealMethod().when(contactPointCsvService).getActualCsvModels(file);
        when(contactPointCsvService.getActualCsvModels(file)).thenReturn(List.of(ContactPointCsvTestData.getCsvModel()));
        when(contactPointCsvService.getCsvModelsToUpdate(file, MIN_LOCAL_DATE)).thenReturn(List.of(ContactPointCsvTestData.getCsvModel()));

        when(prmClient.importContactPoints(any())).thenReturn(Collections.emptyList());
        doNothing().when(mailProducerService).produceMailNotification(any());

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(FULL_PATH_FILENAME_JOB_PARAMETER, file.getAbsolutePath())
                .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
        // when
        JobExecution jobExecution = jobLauncher.run(importTicketCounterCsvJob, jobParameters);
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        // then
        assertThat(actualJobInstance.getJobName()).isEqualTo(IMPORT_TICKET_COUNTER_CSV_JOB_NAME);
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
    }
}
