package ch.sbb.atlas.export;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.atlas.export.exception.ExportException;
import ch.sbb.atlas.export.model.VersionCsvModel;
import ch.sbb.atlas.model.entity.BaseVersion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseExportService<T extends BaseVersion> {

  private final FileService fileService;
  private final AmazonService amazonService;

  public List<URL> exportFullVersions() {
    List<URL> urls = new ArrayList<>();
    File fullVersionsCsv = getFullVersionsCsv();
    urls.add(putZipFile(fullVersionsCsv));
    return urls;
  }

  public List<URL> exportFullVersionsAllFormats() {
    List<URL> urls = exportFullVersions();
    File fullVersionsJson = getFullVersionsJson();
    urls.add(putGzFile(fullVersionsJson));
    return urls;
  }

  public List<URL> exportActualVersions() {
    List<URL> urls = new ArrayList<>();
    File actualVersionsCsv = getActualVersionsCsv();
    urls.add(putZipFile(actualVersionsCsv));
    return urls;
  }

  public List<URL> exportActualVersionsAllFormats() {
    List<URL> urls = exportActualVersions();
    File actualVersionsJson = getActualVersionsJson();
    urls.add(putGzFile(actualVersionsJson));
    return urls;
  }

  public List<URL> exportFutureTimetableVersions() {
    List<URL> urls = new ArrayList<>();
    File futureTimetableVersionsCsv = getFutureTimetableVersionsCsv();
    urls.add(putZipFile(futureTimetableVersionsCsv));
    return urls;
  }

  public List<URL> exportFutureTimetableVersionsAllFormats() {
    List<URL> urls = exportFutureTimetableVersions();
    File futureTimetableVersionsJson = getFutureTimetableVersionsJson();
    urls.add(putGzFile(futureTimetableVersionsJson));
    return urls;
  }

  URL putZipFile(File zipFile) {
    try {
      URL url = amazonService.putZipFile(AmazonBucket.EXPORT, zipFile, getDirectory());
      log.info("Export - ZIP File {} Successfully Put to the directory {}: {}", zipFile.getName(), getDirectory(), url);
      return url;
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new ExportException(zipFile, e);
    }
  }

  URL putGzFile(File gZipFile) {
    try {
      URL url = amazonService.putGzipFile(AmazonBucket.EXPORT, gZipFile, getDirectory());
      log.info("Export - ZIP File {} Successfully Put to the directory {}: {}", gZipFile.getName(), getDirectory(), url);
      return url;
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new ExportException(gZipFile, e);
    }
  }
  
  protected File createCsvFile(List<T> versions, ExportType exportType) {

    File csvFile = createFile(exportType, ".csv");

    List<? extends VersionCsvModel> versionCsvModels = convertToCsvModel(versions);

    ObjectWriter objectWriter = getObjectWriter();
    return CsvExportWriter.writeToFile(csvFile, versionCsvModels, objectWriter);
  }

  protected File createJsonFile(List<T> versions, ExportType exportType) {

    File jsonFile = createFile(exportType, ".json");

    List<? extends BaseVersionModel> versionJsonModels = convertToJsonModel(versions);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    try {
      objectMapper
              .writeValue(jsonFile, versionJsonModels);
    } catch (IOException e) {
      throw new ExportException(jsonFile, e);
    }

    return jsonFile;
  }

  protected abstract ObjectWriter getObjectWriter();

  protected abstract List<VersionCsvModel> convertToCsvModel(List<T> versions);

  protected List<BaseVersionModel> convertToJsonModel(List<T> versions){
    throw new UnsupportedOperationException();
  }

  protected abstract String getDirectory();

  protected abstract File getFullVersionsCsv();

  protected File getFullVersionsJson() {
    throw new UnsupportedOperationException();
  }

  protected abstract File getActualVersionsCsv();

  protected File getActualVersionsJson() {
    throw new UnsupportedOperationException();
  }

  protected abstract File getFutureTimetableVersionsCsv();

  protected File getFutureTimetableVersionsJson() {
    throw new UnsupportedOperationException();
  }

  protected abstract String getFileName();

  protected File createFile(ExportType exportType, String extension) {
    String dir = fileService.getDir();
    String actualDate = LocalDate.now()
            .format(DateTimeFormatter.ofPattern(
                    AtlasApiConstants.DATE_FORMAT_PATTERN));
    return new File(dir + exportType.getFileTypePrefix() + getFileName() + actualDate + extension);
  }

}
