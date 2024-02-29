package ch.sbb.importservice.integration;

import ch.sbb.atlas.imports.prm.relation.RelationCsvModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.testdata.prm.RelationCsvTestData;
import ch.sbb.importservice.client.PrmClient;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.MailProducerService;
import ch.sbb.importservice.service.csv.RelationCsvService;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static ch.sbb.importservice.service.JobHelperService.MIN_LOCAL_DATE;
import static ch.sbb.importservice.utils.JobDescriptionConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
public class ImportRelationIntegrationTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier(IMPORT_RELATION_CSV_JOB_NAME)
    private Job importRelationCsvJob;

    @MockBean
    private RelationCsvService relationCsvService;

    @MockBean
    private PrmClient prmClient;

    @MockBean
    private FileHelperService fileHelperService;

    @MockBean
    private MailProducerService mailProducerService;

    @Test
    void shouldExecuteImportRelationJobDownloadingFileFromS3() throws Exception {
        // given
        List<RelationCsvModel> relationCsvModels = List.of(RelationCsvTestData.getCsvModel());
        when(relationCsvService.getActualCsvModelsFromS3()).thenReturn(relationCsvModels);

        doNothing().when(mailProducerService).produceMailNotification(any());
        when(prmClient.importPlatforms(any())).thenReturn(Collections.emptyList());

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(EXECUTION_TYPE_PARAMETER, EXECUTION_BATCH_PARAMETER)
                .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
        // when
        JobExecution jobExecution = jobLauncher.run(importRelationCsvJob, jobParameters);
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        // then
        assertThat(actualJobInstance.getJobName()).isEqualTo(IMPORT_RELATION_CSV_JOB_NAME);
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());

        verify(mailProducerService, times(1)).produceMailNotification(any());
        verify(relationCsvService, times(1)).getActualCsvModelsFromS3();
    }

    @Test
    void shouldExecuteImportRelationJobFromGivenFile() throws Exception {
        // given
        File file = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("PRM_CONNECTIONS.csv")).getFile());
        when(fileHelperService.downloadImportFileFromS3(any())).thenReturn(file);

        doCallRealMethod().when(relationCsvService).getActualCsvModels(file);
        when(relationCsvService.getActualCsvModels(file)).thenReturn(List.of(RelationCsvTestData.getCsvModel()));
        when(relationCsvService.getCsvModelsToUpdate(file, MIN_LOCAL_DATE)).thenReturn(List.of(RelationCsvTestData.getCsvModel()));

        when(prmClient.importPlatforms(any())).thenReturn(Collections.emptyList());
        doNothing().when(mailProducerService).produceMailNotification(any());

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(FULL_PATH_FILENAME_JOB_PARAMETER, file.getAbsolutePath())
                .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
        // when
        JobExecution jobExecution = jobLauncher.run(importRelationCsvJob, jobParameters);
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        // then
        assertThat(actualJobInstance.getJobName()).isEqualTo(IMPORT_RELATION_CSV_JOB_NAME);
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
    }
}
