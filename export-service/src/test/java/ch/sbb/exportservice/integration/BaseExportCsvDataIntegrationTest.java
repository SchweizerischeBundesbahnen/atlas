package ch.sbb.exportservice.integration;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.imports.bulk.AtlasCsvReader;
import ch.sbb.exportservice.job.sepodi.servicepoint.model.ServicePointVersionCsvModel;
import ch.sbb.exportservice.job.sepodi.servicepoint.service.ExportServicePointJobService;
import ch.sbb.exportservice.tasklet.delete.DeleteCsvFileTasklet;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public abstract class BaseExportCsvDataIntegrationTest {

  @Autowired
  protected JobLauncher jobLauncher;

  @Autowired
  protected ExportServicePointJobService exportServicePointJobService;

  @MockitoBean
  protected AmazonService amazonService;

  @MockitoBean
  @Qualifier("deleteServicePointCsvFileTaskletV1")
  protected DeleteCsvFileTasklet deleteCsvFileTasklet;

  @Captor
  protected ArgumentCaptor<File> fileArgumentCaptor;

  protected List<ServicePointVersionCsvModel> parseCsv(File file) throws IOException {
    MappingIterator<ServicePointVersionCsvModel> mappingIterator = AtlasCsvReader.CSV_MAPPER.readerFor(
        ServicePointVersionCsvModel.class).with(AtlasCsvReader.CSV_SCHEMA).readValues(new FileInputStream(file));
    List<ServicePointVersionCsvModel> servicePoints = new ArrayList<>();

    while (mappingIterator.hasNext()) {
      servicePoints.add(mappingIterator.next());
    }
    return servicePoints;
  }

}
