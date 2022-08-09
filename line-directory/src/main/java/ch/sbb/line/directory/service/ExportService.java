package ch.sbb.line.directory.service;

import static java.util.stream.Collectors.toList;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.line.directory.entity.LineVersionCsvModel;
import ch.sbb.line.directory.repository.LineVersionRepository;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExportService {

  private final LineVersionRepository lineVersionRepository;
  private final FileService fileService;


  public File getAllLineVersionsCsv() {
    try {
      File csvFile = createCsvFile();
      AtlasCsvMapper atlasCsvMapper = new AtlasCsvMapper(LineVersionCsvModel.class);

      List<LineVersionCsvModel> lineVersionCsvModels =
          lineVersionRepository.getAllLineVersions()
                               .stream()
                               .map(LineVersionCsvModel::toCsvModel)
                               .collect(toList());

      ObjectWriter objectWriter = atlasCsvMapper.getCsvMapper().writerFor(LineVersionCsvModel.class)
                                                .with(atlasCsvMapper.getCsvSchema());
      objectWriter.writeValues(csvFile).writeAll(lineVersionCsvModels);
      return csvFile;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  private File createCsvFile() {
    String dir = fileService.getDir();
    String actualDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    return new File(dir + "line_version_" + actualDate + ".csv");
  }

  @Getter
  private static class AtlasCsvMapper {

    private final CsvMapper csvMapper;
    private final CsvSchema csvSchema;

    AtlasCsvMapper(Class aClass) {
      this.csvMapper = createCsvMapper();
      this.csvSchema = this.csvMapper.schemaFor(aClass).withHeader();
    }

    private CsvMapper createCsvMapper() {
      CsvMapper mapper = new CsvMapper();
      mapper.registerModule(new JavaTimeModule());
      mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      return mapper;
    }

  }

}
