package ch.sbb.importservice.service;

import ch.sbb.atlas.imports.bulk.AtlasCsvReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

@Slf4j
@UtilityClass
public class ExcelToCsvConverter {

  public static File convertToCsv(File excelFile) {
    try (Workbook sheets = WorkbookFactory.create(excelFile)) {
      Sheet sheet = sheets.getSheetAt(0);

      String csv = getSheetAsCsv(sheet);

      return writeStringToFile(excelFile, csv);
    } catch (Exception e) {
      throw new IllegalStateException();
    }
  }

  private static String getSheetAsCsv(Sheet sheet) {
    StringBuilder csv = new StringBuilder();
    sheet.rowIterator().forEachRemaining(row -> {
      List<String> rowContent = new ArrayList<>();
      row.cellIterator().forEachRemaining(cell -> {
        rowContent.add(cell.getStringCellValue());
      });
      csv.append(String.join(String.valueOf(AtlasCsvReader.CSV_COLUMN_SEPARATOR), rowContent));
      csv.append(System.lineSeparator());
    });
    return csv.toString();
  }

  private static File writeStringToFile(File excelFile, String csv) throws IOException {
    File csvFile = new File(excelFile.getName() + ".csv");
    Files.writeString(csvFile.toPath(), csv);
    return csvFile;
  }

}
