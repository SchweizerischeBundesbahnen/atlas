package ch.sbb.atlas.export;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;

@Getter
public class AtlasCsvMapper {

  private final ObjectWriter objectWriter;

  public AtlasCsvMapper(Class<?> aClass) {
    this(aClass, null);
  }

  public AtlasCsvMapper(Class<?> aClass, PropertyNamingStrategy namingStrategy) {
    CsvMapper csvMapper = createCsvMapper();
    if (namingStrategy != null) {
      csvMapper.setPropertyNamingStrategy(namingStrategy);
    }
    CsvSchema csvSchema = csvMapper.schemaFor(aClass).withHeader().withColumnSeparator(';');
    this.objectWriter = csvMapper.writerFor(aClass).with(csvSchema);
  }

  private CsvMapper createCsvMapper() {
    CsvMapper mapper = new CsvMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
  }

}
