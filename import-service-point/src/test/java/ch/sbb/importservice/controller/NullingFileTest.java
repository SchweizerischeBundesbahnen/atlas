package ch.sbb.importservice.controller;

import ch.sbb.atlas.imports.AtlasCsvReader;
import ch.sbb.atlas.imports.BulkImportUpdateContainer;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.service.sepodi.service.point.update.ServicePointUpdateCsvModel;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;

@IntegrationTest
class NullingFileTest  {

  @Test
  void shouldAcceptGenericBulkImportWithFile() throws Exception {
    File file = new File(this.getClass().getClassLoader().getResource("service-point-update.csv").getFile());
    List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> servicePointUpdates = AtlasCsvReader.readLinesFromFileWithNullingValue(file, ServicePointUpdateCsvModel.class);
    System.out.println(servicePointUpdates);
  }
}