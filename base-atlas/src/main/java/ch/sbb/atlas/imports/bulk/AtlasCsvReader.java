package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.exception.CsvException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AtlasCsvReader {

  public static final CsvMapper CSV_MAPPER = new CsvMapper().enable(Feature.EMPTY_STRING_AS_NULL);
  public static final CsvSchema CSV_SCHEMA = CsvSchema.emptySchema()
      .withHeader()
      .withColumnSeparator(';');
  public static final String NULLING_VALUE = "<null>";

  public <T> List<T> readLinesFromFile(File file, Class<T> clazz) {
    List<T> mappedObjects = new ArrayList<>();

    try (MappingIterator<T> mappingIterator = AtlasCsvReader.CSV_MAPPER.readerFor(clazz)
        .with(AtlasCsvReader.CSV_SCHEMA)
        .readValues(file)) {
      while (mappingIterator.hasNext()) {
        mappedObjects.add(mappingIterator.next());
      }
    } catch (IOException e) {
      throw new CsvException(e);
    }

    return mappedObjects;
  }

  public <T> List<BulkImportUpdateContainer<T>> readLinesFromFileWithNullingValue(File file, Class<T> clazz) {
    List<BulkImportUpdateContainer<T>> mappedObjects = new ArrayList<>();

    String header = "";

    try (Scanner scanner = new Scanner(file)) {
      for (int lineNumber = 0; scanner.hasNext(); lineNumber++) {
        String line = scanner.nextLine();
        if (lineNumber == 0) {
          header = line;
          header += "\n";
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

    line = line.replaceAll(NULLING_VALUE, "");

    try (MappingIterator<T> mappingIterator = AtlasCsvReader.CSV_MAPPER.readerFor(clazz)
        .with(AtlasCsvReader.CSV_SCHEMA)
        .readValues(header + line)) {
      T object = mappingIterator.next();
      return BulkImportUpdateContainer.<T>builder()
          .lineNumber(lineNumber)
          .object(object)
          .attributesToNull(toNullAttributes)
          .build();
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
