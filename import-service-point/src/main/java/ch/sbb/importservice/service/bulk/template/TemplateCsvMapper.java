package ch.sbb.importservice.service.bulk.template;

import ch.sbb.importservice.serializer.LocalDateSerializer;
import ch.sbb.importservice.serializer.PipedSetSerializer;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;

@Getter
public class TemplateCsvMapper {

  public static final char SEPARATOR = ';';
  private final ObjectWriter objectWriter;

  public TemplateCsvMapper(Class<?> aClass) {
    CsvMapper csvMapper = createCsvMapper();

    CsvSchema csvSchema = csvMapper
        .schemaFor(aClass)
        .withoutEscapeChar()
        .withoutQuoteChar()
        .withHeader()
        .withColumnSeparator(SEPARATOR);

    this.objectWriter = csvMapper.writerFor(aClass).with(csvSchema);
  }

  private CsvMapper createCsvMapper() {
    CsvMapper mapper = new CsvMapper();

    JavaTimeModule javaTimeModule = new JavaTimeModule();
    LocalDateSerializer localDateSerializer = new LocalDateSerializer();
    javaTimeModule.addSerializer(LocalDate.class,localDateSerializer);
    mapper.registerModule(javaTimeModule);

    SimpleModule module = new SimpleModule();
    module.addSerializer(Set.class,new PipedSetSerializer());
    mapper.registerModule(module);
    return mapper;
  }

}
