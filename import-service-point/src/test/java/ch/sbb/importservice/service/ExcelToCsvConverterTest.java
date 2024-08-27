package ch.sbb.importservice.service;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.ImportFiles;
import java.io.File;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class ExcelToCsvConverterTest {

  @Autowired
  private ExcelToCsvConverter excelToCsvConverter;

  @Test
  void shouldReadServicePointUpdateXlsCorrectly() {
    File file = ImportFiles.getFileByPath("import-files/valid/service-point-update.xls");

    File csvFile = excelToCsvConverter.convertToCsv(file);

    ImportFiles.assertThatFileContainsExpectedServicePointUpdate(csvFile);
  }

  @Test
  void shouldReadServicePointUpdateXlsxCorrectly() {
    File file = ImportFiles.getFileByPath("import-files/valid/service-point-update.xlsx");

    File csvFile = excelToCsvConverter.convertToCsv(file);

    ImportFiles.assertThatFileContainsExpectedServicePointUpdate(csvFile);
  }

}