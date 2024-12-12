package ch.sbb.importservice.service;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.imports.bulk.AtlasCsvReader;
import ch.sbb.importservice.exception.ExcelToCsvConversionException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelToCsvConverter {

  private static final Predicate<Cell> IS_DATE_VALUE = cell -> cell.getCellStyle().getDataFormatString().equals("m/d/yy");
  private static final DoublePredicate IS_INT = doubleValue -> Double.isFinite(doubleValue)
      && Double.compare(doubleValue, StrictMath.rint(doubleValue)) == 0;

  private final FileService fileService;

  public File convertToCsv(File excelFile) {
    try (Workbook sheets = WorkbookFactory.create(excelFile)) {
      Sheet sheet = sheets.getSheetAt(0);

      String csv = getSheetAsCsv(sheet);

      return writeStringToFile(excelFile, csv);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private String getSheetAsCsv(Sheet sheet) {
    StringBuilder csv = new StringBuilder();

    for (int i = 0; i < sheet.getLastRowNum() + 1; i++) {
      Row row = sheet.getRow(i);

      List<String> rowContent = new ArrayList<>();

      for (int j = 0; j < row.getLastCellNum(); j++) {
        Cell cell = row.getCell(j);
        if (cell == null) {
          rowContent.add("");
        } else {
          rowContent.add(getCellValue(cell));
        }
      }

      csv.append(String.join(String.valueOf(AtlasCsvReader.CSV_COLUMN_SEPARATOR), rowContent));
      csv.append(System.lineSeparator());
    }

    return csv.toString();
  }

  static String getCellValue(Cell cell) {
    switch (cell.getCellType()) {
      case NUMERIC -> {
        if (IS_DATE_VALUE.test(cell)) {
          LocalDate cellAsDate = cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
          return cellAsDate.format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN_CH));
        }
        double numericCellValue = cell.getNumericCellValue();
        if (IS_INT.test(numericCellValue)) {
          return String.valueOf((int) numericCellValue);
        }
        return String.valueOf(numericCellValue);
      }
      case STRING -> {
        return StringUtils.trim(cell.getStringCellValue());
      }
      case BLANK -> {
        return "";
      }
      case BOOLEAN -> {
        return String.valueOf(cell.getBooleanCellValue());
      }
      default -> throw new ExcelToCsvConversionException(cell);
    }
  }

  private File writeStringToFile(File excelFile, String csv) throws IOException {
    File csvFile = new File(fileService.getDir() + File.separator + excelFile.getName() + ".csv");
    Files.writeString(csvFile.toPath(), csv);
    return csvFile;
  }

}
