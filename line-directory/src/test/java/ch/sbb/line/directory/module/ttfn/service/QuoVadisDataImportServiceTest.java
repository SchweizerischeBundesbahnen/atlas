package ch.sbb.line.directory.module.ttfn.service;

import ch.sbb.atlas.model.controller.IntegrationTest;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class QuoVadisDataImportServiceTest {

  @Autowired
  private QuoVadisDataImportService quoVadisDataImportService;

  @Test
  void importDataFromQuoVadis() throws IOException {
    File file = new File("src/test/resources/quovadis_ttfn.csv");
    quoVadisDataImportService.importDataFromQuoVadis(file);

  }

}