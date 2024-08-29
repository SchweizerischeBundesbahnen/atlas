package ch.sbb.importservice.service.bulk.reader;

import ch.sbb.atlas.exception.CsvException;
import ch.sbb.atlas.imports.bulk.AtlasCsvReader;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportStatus;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
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

  public <T> List<BulkImportUpdateContainer<T>> readLinesFromFileWithNullingValue(File file, Class<T> clazz) {
    List<BulkImportUpdateContainer<T>> mappedObjects = new ArrayList<>();

    String header = "";

    try (Scanner scanner = new Scanner(file)) {
      for (int lineNumber = 0; scanner.hasNext(); lineNumber++) {
        String line = scanner.nextLine();
        if (lineNumber == 0) {
          header = line + "\n";
        } else {
          mappedObjects.add(readObject(clazz, header, line, lineNumber));
        }
      }
    } catch (IOException ex) {
      throw new CsvException(ex);
    }

    return mappedObjects;
  }

  private static <T> BulkImportUpdateContainer<T> readObject(Class<T> clazz, String header, String line, int lineNumber)
      throws IOException {
    List<String> toNullAttributes = calculateAttributesToNull(header, line);

    line = line.replace(NULLING_VALUE, "");

    CsvExceptionHandler csvExceptionHandler = new CsvExceptionHandler();
    try (MappingIterator<T> mappingIterator = AtlasCsvReader.CSV_MAPPER
        .registerModule(PipedSetDeserializer.module())
        .addHandler(csvExceptionHandler)
        .readerFor(clazz)
        .with(AtlasCsvReader.CSV_SCHEMA)
        .readValues(header + line)) {
      T object = mappingIterator.next();
      if (csvExceptionHandler.getErrors().isEmpty()) {
        return BulkImportUpdateContainer.<T>builder()
            .lineNumber(lineNumber)
            .object(object)
            .attributesToNull(toNullAttributes)
            .build();
      } else {
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
