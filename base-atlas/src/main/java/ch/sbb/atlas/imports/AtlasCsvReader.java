package ch.sbb.atlas.imports;

import ch.sbb.atlas.exception.CsvException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AtlasCsvReader {

  public static final CsvMapper CSV_MAPPER = new CsvMapper().enable(Feature.EMPTY_STRING_AS_NULL);
  public static final CsvSchema CSV_SCHEMA = CsvSchema.emptySchema()
      .withHeader()
      .withColumnSeparator(';');

  public  <T> List<T> readLinesFromFile(File file, Class<T> clazz) {
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

}
