package ch.sbb.importservice.service.bulk.reader;

import static ch.sbb.atlas.export.utils.StringUtils.NEW_LINE;

import ch.sbb.atlas.exception.CsvException;
import ch.sbb.atlas.export.CsvExportWriter;
import ch.sbb.atlas.imports.bulk.AtlasCsvReader;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportStatus;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class BulkImportCsvReader {

  public static final String NULLING_VALUE = "<null>";

  public static String readHeaderLineIgnoringBom(String rawLine) {
    return rawLine.replaceAll(String.valueOf(CsvExportWriter.UTF_8_BYTE_ORDER_MARK), "");
  }

  public static String getFileHeader(File file) {
    try (Scanner scanner = new Scanner(file)) {
      return getFileHeader(scanner).getLast();
    } catch (IOException e) {
      return null;
    }
  }

  public static List<String> getFileHeader(Scanner scanner) {
    List<String> lines = new ArrayList<>();
    while (scanner.hasNextLine()) {
      final String line = scanner.nextLine();
      lines.add(readHeaderLineIgnoringBom(line));
      if (!line.isEmpty()) {
        return lines;
      }
    }
    lines.add(null);
    return lines;
  }

  public <T> List<BulkImportUpdateContainer<T>> readLinesFromFileWithNullingValue(File file, Class<T> clazz) {
    List<BulkImportUpdateContainer<T>> mappedObjects = new ArrayList<>();

    try (Scanner scanner = new Scanner(file)) {
      final List<String> fileHeader = getFileHeader(scanner);
      final String header = fileHeader.getLast() + NEW_LINE;

      for (int lineNumber = fileHeader.size() + 1; scanner.hasNextLine(); lineNumber++) {
        final String line = readHeaderLineIgnoringBom(scanner.nextLine());
        if (!line.matches(";*")) {
          mappedObjects.add(readObject(clazz, header, line, lineNumber));
        }
      }
    } catch (IOException ex) {
      throw new CsvException(ex);
    }

    return mappedObjects;
  }

  static <T> BulkImportUpdateContainer<T> readObject(Class<T> clazz, String header, String line,
      int lineNumber)
      throws IOException {
    List<String> toNullAttributes = calculateAttributesToNull(header, line);

    line = line.replace(NULLING_VALUE, "");

    CsvExceptionHandler csvExceptionHandler = new CsvExceptionHandler();
    try (MappingIterator<T> mappingIterator = AtlasCsvReader.CSV_MAPPER
        .registerModule(PipedSetDeserializer.module())
        .enable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)
        .addHandler(csvExceptionHandler)
        .readerFor(clazz)
        .with(AtlasCsvReader.CSV_SCHEMA)
        .readValues(header + line)) {
      T object = mappingIterator.next();

      // Successful parsing
      if (csvExceptionHandler.getErrors().isEmpty()) {
        return BulkImportUpdateContainer.<T>builder()
            .lineNumber(lineNumber)
            .object(object)
            .attributesToNull(toNullAttributes)
            .build();
      }
      // Error on csv parsing
      else {
        return BulkImportUpdateContainer.<T>builder()
            .lineNumber(lineNumber)
            .bulkImportLogEntry(BulkImportLogEntry.builder()
                .lineNumber(lineNumber)
                .status(BulkImportStatus.DATA_VALIDATION_ERROR)
                .errors(new ArrayList<>(csvExceptionHandler.getDataMappingErrors()))
                .build())
            .build();
      }
    }
  }

  private static List<String> calculateAttributesToNull(String header, String line) throws IOException {
    List<String> toNullAttributes = new ArrayList<>();

    try (MappingIterator<Map<String, String>> stringMap = AtlasCsvReader.CSV_MAPPER.readerForMapOf(String.class)
        .with(AtlasCsvReader.CSV_SCHEMA)
        .readValues(header + line)) {

      Map<String, String> lineAsMap = stringMap.next();
      lineAsMap.forEach((key, value) -> {
        if (NULLING_VALUE.equals(value)) {
          toNullAttributes.add(key);
        }
      });
    }

    return toNullAttributes;
  }

}
