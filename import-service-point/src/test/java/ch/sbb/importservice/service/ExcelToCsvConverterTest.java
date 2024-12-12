package ch.sbb.importservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.ImportFiles;
import java.io.File;
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