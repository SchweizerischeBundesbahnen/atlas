package ch.sbb.line.directory.service.export;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.model.entity.BaseVersion;
import ch.sbb.atlas.model.exception.ExportException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Getter;

public abstract class BaseExportService<T extends BaseVersion> {

  private final FileService fileService;
  private final AmazonService amazonService;

  public BaseExportService(FileService fileService, AmazonService amazonService) {
    this.fileService = fileService;
    this.amazonService = amazonService;
  }

  URL putCsvFile(File csvFile) {
    try {
      return amazonService.putFile(csvFile, getDirectory());
    } catch (IOException e) {
      throw new ExportException(csvFile, e);
    }
  }

  URL putZipFile(File zipFile) {
    try {
      return amazonService.putZipFile(zipFile, getDirectory());
    } catch (IOException e) {
      throw new ExportException(zipFile, e);
    }
  }

  protected abstract String getDirectory();

  protected abstract File getFullVersionsCsv();

  protected abstract File getActualVersionsCsv();

  protected abstract File getFutureTimetableVersionsCsv();

  public URL exportFullVersionsCsv() {
    File fullVersionsCsv = getFullVersionsCsv();
    return putCsvFile(fullVersionsCsv);
  }

  public URL exportFullVersionsCsvZip() {
    File csvFile = getFullVersionsCsv();
    return putZipFile(csvFile);
  }

  public URL exportActualVersionsCsv() {
    File fullVersionsCsv = getActualVersionsCsv();
    return putCsvFile(fullVersionsCsv);
  }

  public URL exportActualVersionsCsvZip() {
    File csvFile = getActualVersionsCsv();
    return putZipFile(csvFile);
  }

  public URL exportFutureTimetableVersionsCsv() {
    File csvFile = getFutureTimetableVersionsCsv();
    return putCsvFile(csvFile);
  }

  public URL exportFutureTimetableVersionsCsvZip() {
    File csvFile = getFutureTimetableVersionsCsv();
    return putZipFile(csvFile);
  }

  protected File createFile(ExportType exportType) {
    String dir = fileService.getDir();
    String actualDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    return new File(dir + exportType.getFilePrefix() + getFileName() + actualDate + ".csv");
  }

  protected String getFileName() {
    throw new IllegalStateException("You have to define a file name!");
  }

  @Getter
  protected static class AtlasCsvMapper {

    private final CsvMapper csvMapper;
    private final CsvSchema csvSchema;

    AtlasCsvMapper(Class<?> aClass) {
      this.csvMapper = createCsvMapper();
      this.csvSchema = this.csvMapper
          .schemaFor(aClass)
          .withHeader()
          .withColumnSeparator(';');
    }

    private CsvMapper createCsvMapper() {
      CsvMapper mapper = new CsvMapper();
      mapper.registerModule(new JavaTimeModule());
      mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      return mapper;
    }

  }

}
