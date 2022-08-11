package ch.sbb.line.directory.service.export;

import static java.util.stream.Collectors.toList;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersionCsvModel;
import ch.sbb.line.directory.repository.LineVersionRepository;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
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

  public File getFullLineVersionsCsv() {
    List<LineVersion> fullLineVersions = lineVersionRepository.getFullLineVersions();
    return createCsvFile(fullLineVersions, ExportType.FULL);
  }

  public File getActualLineVersionsCsv() {
    List<LineVersion> actualLineVersions = lineVersionRepository.getActualLineVersions(
        LocalDate.now());
    return createCsvFile(actualLineVersions, ExportType.ACTUAL_DATE);
  }

  public File getActualFutureTimetableLineVersionsCsv() {
    List<LineVersion> actualLineVersions = lineVersionRepository.getActualLineVersions(
        ExportHelper.getFutureTimetableDate(LocalDate.now()));
    return createCsvFile(actualLineVersions, ExportType.FUTURE_TIMETABLE);
  }

  private File createCsvFile(List<LineVersion> lineVersions, ExportType exportType) {

    File csvFile = createFile(exportType);
    AtlasCsvMapper atlasCsvMapper = new AtlasCsvMapper(LineVersionCsvModel.class);

    List<LineVersionCsvModel> lineVersionCsvModels =
        lineVersions.stream()
                    .map(LineVersionCsvModel::toCsvModel)
                    .collect(toList());

    ObjectWriter objectWriter = atlasCsvMapper.getCsvMapper().writerFor(LineVersionCsvModel.class)
                                              .with(atlasCsvMapper.getCsvSchema());
    try (SequenceWriter sequenceWriter = objectWriter.writeValues(csvFile)) {
      sequenceWriter.writeAll(lineVersionCsvModels);
      return csvFile;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private File createFile(ExportType exportType) {
    String dir = fileService.getDir();
    String actualDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    return new File(dir + exportType.getFilePrefix() + "line_versions_" + actualDate + ".csv");
  }

  @Getter
  private static class AtlasCsvMapper {

    private final CsvMapper csvMapper;
    private final CsvSchema csvSchema;

    AtlasCsvMapper(Class<?> aClass) {
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
