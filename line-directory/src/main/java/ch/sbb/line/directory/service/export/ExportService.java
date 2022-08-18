package ch.sbb.line.directory.service.export;

import static java.util.stream.Collectors.toList;

import ch.sbb.atlas.amazon.helper.FutureTimetableHelper;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.model.exception.ExportException;
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
import java.net.URL;
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
  private final AmazonService amazonService;

  public URL exportFullLineVersionsCsv() {
    File csvFile = getFullLineVersionsCsv();
    return putCsvFile(csvFile);
  }

  public URL exportFullLineVersionsCsvZip() {
    File csvFile = getFullLineVersionsCsv();
    return putZipFile(csvFile);
  }

  public URL exportActualLineVersionsCsv() {
    File csvFile = getActualLineVersionsCsv();
    return putCsvFile(csvFile);
  }

  public URL exportActualLineVersionsCsvZip() {
    File csvFile = getActualLineVersionsCsv();
    return putZipFile(csvFile);
  }

  public URL exportFutureTimetableLineVersionsCsv() {
    File csvFile = getActualFutureTimetableLineVersionsCsv();
    return putCsvFile(csvFile);
  }

  public URL exportFutureTimetableLineVersionsCsvZip() {
    File csvFile = getActualFutureTimetableLineVersionsCsv();
    return putZipFile(csvFile);
  }

  URL putCsvFile(File csvFile) {
    try {
      return amazonService.putFile(csvFile);
    } catch (IOException e) {
      throw new ExportException(csvFile, e);
    }
  }

  URL putZipFile(File zipFile) {
    try {
      return amazonService.putZipFile(zipFile);
    } catch (IOException e) {
      throw new ExportException(zipFile, e);
    }
  }

  private File getFullLineVersionsCsv() {
    List<LineVersion> fullLineVersions = lineVersionRepository.getFullLineVersions();
    return createCsvFile(fullLineVersions, ExportType.FULL);
  }

  private File getActualLineVersionsCsv() {
    List<LineVersion> actualLineVersions = lineVersionRepository.getActualLineVersions(
        LocalDate.now());
    return createCsvFile(actualLineVersions, ExportType.ACTUAL_DATE);
  }

  private File getActualFutureTimetableLineVersionsCsv() {
    List<LineVersion> actualLineVersions = lineVersionRepository.getActualLineVersions(
        FutureTimetableHelper.geTimetableYearChangeDateToExportData(LocalDate.now()));
    return createCsvFile(actualLineVersions, ExportType.FUTURE_TIMETABLE);
  }

  private File createCsvFile(List<LineVersion> lineVersions, ExportType exportType) {

    File csvFile = createFile(exportType);
    AtlasCsvMapper atlasCsvMapper = new AtlasCsvMapper(LineVersionCsvModel.class);

    List<LineVersionCsvModel> lineVersionCsvModels =
        lineVersions.stream()
                    .map(LineVersionCsvModel::toCsvModel)
                    .collect(toList());

    ObjectWriter objectWriter = atlasCsvMapper.getCsvMapper()
                                              .writerFor(LineVersionCsvModel.class)
                                              .with(atlasCsvMapper.getCsvSchema());
    try (SequenceWriter sequenceWriter = objectWriter.writeValues(csvFile)) {
      sequenceWriter.writeAll(lineVersionCsvModels);
      return csvFile;
    } catch (IOException e) {
      throw new ExportException(csvFile, e);
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
