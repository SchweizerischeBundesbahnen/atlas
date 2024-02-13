package ch.sbb.exportservice.integration;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.imports.DidokCsvMapper;
import ch.sbb.exportservice.model.ServicePointVersionCsvModel;
import ch.sbb.exportservice.tasklet.FileCsvDeletingTasklet;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;

public abstract class BaseExportCsvDataIntegrationTest {

  @Autowired
  protected JobLauncher jobLauncher;

  @Autowired
  @Qualifier(EXPORT_SERVICE_POINT_CSV_JOB_NAME)
  protected Job exportServicePointCsvJob;

  @MockBean
  protected AmazonService amazonService;

  @MockBean
  @Qualifier("fileCsvDeletingTasklet")
  protected FileCsvDeletingTasklet fileCsvDeletingTasklet;

  @Captor
  protected ArgumentCaptor<File> fileArgumentCaptor;

  protected List<ServicePointVersionCsvModel> parseCsv(File file) throws IOException {
    MappingIterator<ServicePointVersionCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointVersionCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(new FileInputStream(file));
    List<ServicePointVersionCsvModel> servicePoints = new ArrayList<>();

    while (mappingIterator.hasNext()) {
      servicePoints.add(mappingIterator.next());
    }
    return servicePoints;
  }

}
