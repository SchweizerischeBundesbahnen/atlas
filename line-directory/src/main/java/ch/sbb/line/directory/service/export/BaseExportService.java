package ch.sbb.line.directory.service.export;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.model.api.AtlasApiConstants;
import ch.sbb.atlas.model.entity.BaseVersion;
import ch.sbb.atlas.model.exception.ExportException;
import ch.sbb.line.directory.entity.VersionCsvModel;
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
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public abstract class BaseExportService<T extends BaseVersion> {

  private final FileService fileService;
  private final AmazonService amazonService;

  public List<URL> exportFullVersions() {
    List<URL> urls = new ArrayList<>();
    File fullVersionsCsv = getFullVersionsCsv();
    urls.add(putCsvFile(fullVersionsCsv));
    urls.add(putZipFile(fullVersionsCsv));
    return urls;
  }

  public List<URL> exportActualVersions() {
    List<URL> urls = new ArrayList<>();
    File actualVersionsCsv = getActualVersionsCsv();
    urls.add(putCsvFile(actualVersionsCsv));
    urls.add(putZipFile(actualVersionsCsv));
    return urls;
  }

  public List<URL> exportFutureTimetableVersions() {
    List<URL> urls = new ArrayList<>();
    File futureTimetableVersionsCsv = getFutureTimetableVersionsCsv();
    urls.add(putCsvFile(futureTimetableVersionsCsv));
    urls.add(putZipFile(futureTimetableVersionsCsv));
    return urls;
  }

  URL putCsvFile(File csvFile) {
    try {
      return amazonService.putFile(csvFile, getDirectory());
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new ExportException(csvFile, e);
    }
  }

  URL putZipFile(File zipFile) {
    try {
      return amazonService.putZipFile(zipFile, getDirectory());
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new ExportException(zipFile, e);
    }
  }

  protected File createCsvFile(List<T> versions, ExportType exportType) {

    File csvFile = createFile(exportType);

    List<? extends VersionCsvModel> versionCsvModels = convertToCsvModel(versions);

    ObjectWriter objectWriter = getObjectWriter();
    try (SequenceWriter sequenceWriter = objectWriter.writeValues(csvFile)) {
      sequenceWriter.writeAll(versionCsvModels);
      return csvFile;
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new ExportException(csvFile, e);
    }
  }

  protected abstract ObjectWriter getObjectWriter();

  protected abstract List<? extends VersionCsvModel> convertToCsvModel(List<T> versions);

  protected abstract String getDirectory();

  protected abstract File getFullVersionsCsv();

  protected abstract File getActualVersionsCsv();

  protected abstract File getFutureTimetableVersionsCsv();

  protected abstract String getFileName();

  protected File createFile(ExportType exportType) {
    String dir = fileService.getDir();
    String actualDate = LocalDate.now()
                                 .format(DateTimeFormatter.ofPattern(
                                     AtlasApiConstants.DATE_FORMAT_PATTERN));
    return new File(dir + exportType.getFilePrefix() + getFileName() + actualDate + ".csv");
  }

  @Getter
  protected static class AtlasCsvMapper {

    private final ObjectWriter objectWriter;

    AtlasCsvMapper(Class<?> aClass) {
      CsvMapper csvMapper = createCsvMapper();
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

}
