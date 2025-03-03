package ch.sbb.importservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.ImportFiles;
import ch.sbb.importservice.service.bulk.reader.BulkImportCsvReader;
import java.io.File;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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

  @Test
  void shouldReadServicePointUpdateXlsxWithEmptyLinesCorrectly() {
    File file = ImportFiles.getFileByPath("import-files/valid/service-point-update-with-empty-lines.xlsx");

    File csvFile = excelToCsvConverter.convertToCsv(file);

    List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> servicePointUpdates =
        BulkImportCsvReader.readLinesFromFileWithNullingValue(csvFile, ServicePointUpdateCsvModel.class);

    assertThat(servicePointUpdates).hasSize(2);
    assertThat(servicePointUpdates.get(0).getLineNumber()).isEqualTo(7);
    assertThat(servicePointUpdates.get(1).getLineNumber()).isEqualTo(11);
  }

  @Test
  void shouldIgnoreTabsAndSpacesInCellValues() {
    Cell cell = mock(Cell.class);
    when(cell.getCellType()).thenReturn(CellType.STRING);

    when(cell.getStringCellValue()).thenReturn("\tch:1:sloid:77234:0:01 ");
    String cellValue = ExcelToCsvConverter.getCellValue(cell);
    assertThat(cellValue).isEqualTo("ch:1:sloid:77234:0:01");
  }

  @Test
  void shouldTrimToEmptyStringToHaveValidCSV() {
    Cell cell = mock(Cell.class);
    when(cell.getCellType()).thenReturn(CellType.STRING);

    when(cell.getStringCellValue()).thenReturn(" ");
    String cellValue = ExcelToCsvConverter.getCellValue(cell);
    assertThat(cellValue).isNotNull().isEmpty();
  }

}
