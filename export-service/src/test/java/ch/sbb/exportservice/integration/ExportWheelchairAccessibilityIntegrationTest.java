package ch.sbb.exportservice.integration;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_WHEELCHAIR_ACCESSIBILITY_CSV_JOB_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.export.CsvExportWriter;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.tasklet.delete.FileDeletingTaskletV2;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobInstance;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class ExportWheelchairAccessibilityIntegrationTest extends BaseExportCsvDataIntegrationTest {

  @Autowired @Qualifier(EXPORT_WHEELCHAIR_ACCESSIBILITY_CSV_JOB_NAME) private Job exportWheelchairAccessibilityCsvJob;

  @MockitoBean @Qualifier("deleteWheelchairAccessibilityCsvFileTasklet") private FileDeletingTaskletV2 deleteWheelchairAccessibilityCsvFileTasklet;

  @Test
  @SqlGroup({@Sql(scripts = {"/prm-schema.sql",
      "/prm-basel-sbb-data.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig(dataSource =
      "prmDataSource", transactionManager = "prmTransactionManager", transactionMode = SqlConfig.TransactionMode.ISOLATED)),
      @Sql(scripts = {
          "/prm-drop.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(dataSource = "prmDataSource"
          , transactionManager = "prmTransactionManager", transactionMode = SqlConfig.TransactionMode.ISOLATED))})
  void shouldExecuteWheelchairAccessibilityCsvJob() throws Exception {
    when(amazonService.putZipFileCleanupBoth(any(), fileArgumentCaptor.capture(), any())).thenReturn(
        URI.create("https://sbb.ch").toURL());
    when(deleteWheelchairAccessibilityCsvFileTasklet.execute(any(), any())).thenReturn(null);

    // given
    JobParameters jobParameters = BaseExportJobService.buildJobParameters(new JobParams(ExportTypeV2.ACTUAL));
    // when
    JobExecution jobExecution = jobOperator.start(exportWheelchairAccessibilityCsvJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(EXPORT_WHEELCHAIR_ACCESSIBILITY_CSV_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());

    File exportedCsvFile = fileArgumentCaptor.getValue();
    String fileContent = Files.readString(exportedCsvFile.toPath());
    Files.delete(exportedCsvFile.toPath());

    assertThat(fileContent).isEqualToIgnoringNewLines(CsvExportWriter.UTF_8_BYTE_ORDER_MARK + """
        number;sloid;type;accessibility;validFrom;validTo
        8500010;ch:1:sloid:10;STOP_POINT;NO_INFO;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:3:5;PLATFORM;AUTONOMY;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:22:35;PLATFORM;PRE_REGISTRATION;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:3:6;PLATFORM;AUTONOMY;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:2:4;PLATFORM;AUTONOMY;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:4:8;PLATFORM;AUTONOMY;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:4:7;PLATFORM;AUTONOMY;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:8:16;PLATFORM;AUTONOMY;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:7:15;PLATFORM;AUTONOMY;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:7:14;PLATFORM;AUTONOMY;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:6:11;PLATFORM;AUTONOMY;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:5:10;PLATFORM;AUTONOMY;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:0:944;PLATFORM;NO_INFO;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:6:12;PLATFORM;AUTONOMY;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:21:30;PLATFORM;PRE_REGISTRATION;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:0:20;PLATFORM;AUTONOMY;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:21:31;PLATFORM;PRE_REGISTRATION;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:2:3;PLATFORM;AUTONOMY;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:1:1;PLATFORM;AUTONOMY;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:8:17;PLATFORM;AUTONOMY;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:22:33;PLATFORM;PRE_REGISTRATION;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:1:2;PLATFORM;AUTONOMY;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:0:19;PLATFORM;PRE_REGISTRATION;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:5:9;PLATFORM;AUTONOMY;08.06.2026;08.07.2026
        8500010;ch:1:sloid:10:0:1751;PLATFORM;PRE_REGISTRATION;08.06.2026;08.07.2026
        """);
  }

}
