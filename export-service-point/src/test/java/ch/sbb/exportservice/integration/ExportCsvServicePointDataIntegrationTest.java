package ch.sbb.exportservice.integration;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TYPE_JOB_PARAMETER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.imports.DidokCsvMapper;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.exportservice.BatchDataSourceConfigTest;
import ch.sbb.exportservice.model.ServicePointExportType;
import ch.sbb.exportservice.model.ServicePointVersionCsvModel;
import ch.sbb.exportservice.tasklet.FileCsvDeletingTasklet;
import ch.sbb.exportservice.utils.JobDescriptionConstants;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

@BatchDataSourceConfigTest
@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
public class ExportCsvServicePointDataIntegrationTest {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier(EXPORT_SERVICE_POINT_CSV_JOB_NAME)
  private Job exportServicePointCsvJob;

  @MockBean
  private AmazonService amazonService;

  @MockBean
  private FileCsvDeletingTasklet fileCsvDeletingTasklet;

  @Captor
  private ArgumentCaptor<File> fileArgumentCaptor;

  @Test
  public void shouldExportServicePointToCsvWithCorrectData() throws Exception {
    when(amazonService.putZipFile(any(), fileArgumentCaptor.capture(), any())).thenReturn(new URL("https://sbb.ch"));

    JobParameters jobParameters = new JobParametersBuilder()
        .addString(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, JobDescriptionConstants.EXECUTION_BATCH_PARAMETER)
        .addString(EXPORT_TYPE_JOB_PARAMETER, ServicePointExportType.WORLD_FULL.toString())
        .addLong(JobDescriptionConstants.START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();

    // when
    jobLauncher.run(exportServicePointCsvJob, jobParameters);

    // then
    File exportedCsvFile = fileArgumentCaptor.getValue();
    List<ServicePointVersionCsvModel> exportedCsv = parseCsv(exportedCsvFile);
    Files.delete(exportedCsvFile.toPath());

    ServicePointVersionCsvModel magdenObrist = exportedCsv.stream().filter(i -> i.getNumber().equals(85722413)).findFirst()
        .orElseThrow();
    assertThat(magdenObrist.getBusinessOrganisationOrganisationNumber()).isEqualTo(999);
    assertThat(magdenObrist.getBusinessOrganisationAbbreviationDe()).isEqualTo("SAS-Code");
  }

  private List<ServicePointVersionCsvModel> parseCsv(File file) throws IOException {
    MappingIterator<ServicePointVersionCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointVersionCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(new FileInputStream(file));
    List<ServicePointVersionCsvModel> servicePoints = new ArrayList<>();

    while (mappingIterator.hasNext()) {
      servicePoints.add(mappingIterator.next());
    }
    return servicePoints;
  }

}


