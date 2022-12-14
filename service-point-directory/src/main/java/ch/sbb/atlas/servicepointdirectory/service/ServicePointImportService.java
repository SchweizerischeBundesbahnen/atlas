package ch.sbb.atlas.servicepointdirectory.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ServicePointImportService {

  public static List<ServicePointCsvModel> parseServicePoints(InputStream inputStream)
      throws IOException {
    CsvMapper mapper = new CsvMapper().enable(Feature.EMPTY_STRING_AS_NULL);
    CsvSchema csvSchema = CsvSchema.emptySchema()
        .withHeader()
        .withColumnSeparator(';')
        .withEscapeChar('\\');

    MappingIterator<ServicePointCsvModel> mappingIterator = mapper.readerFor(
        ServicePointCsvModel.class).with(csvSchema).readValues(inputStream);
    int counter = 0;
    List<ServicePointCsvModel> servicePoints = new ArrayList<>();
    while (mappingIterator.hasNext() && counter < 10) {
      servicePoints.add(mappingIterator.next());
      counter++;
    }
    log.info("Parsed {} servicePoints", servicePoints.size());
    return servicePoints;
  }

}
