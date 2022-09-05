package ch.sbb.line.directory.service.export;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
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
import java.util.List;
import lombok.Getter;

public abstract class BaseExportService<T extends BaseVersion> {

  private final FileService fileService;
  private final AmazonService amazonService;

  public BaseExportService(FileService fileService, AmazonService amazonService) {
    this.fileService = fileService;
    this.amazonService = amazonService;
  }

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

  protected File createCsvFile(List<T> lineVersions, ExportType exportType) {

    File csvFile = createFile(exportType);

    List<? extends VersionCsvModel> lineVersionCsvModels = convertToCsvModel(lineVersions);

    ObjectWriter objectWriter = getObjectWriter();
    try (SequenceWriter sequenceWriter = objectWriter.writeValues(csvFile)) {
      sequenceWriter.writeAll(lineVersionCsvModels);
      return csvFile;
    } catch (IOException e) {
      throw new ExportException(csvFile, e);
    }
  }

  protected abstract ObjectWriter getObjectWriter();

  protected abstract List<? extends VersionCsvModel> convertToCsvModel(List<T> versions);

  protected abstract String getDirectory();

  protected abstract File getFullVersionsCsv();

  protected abstract File getActualVersionsCsv();

  protected abstract File getFutureTimetableVersionsCsv();

  protected File createFile(ExportType exportType) {
    String dir = fileService.getDir();
    String actualDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    return new File(dir + exportType.getFilePrefix() + getFileName() + actualDate + ".csv");
  }

  protected abstract String getFileName();

  @Getter
  protected static class AtlasCsvMapper {

    private final ObjectWriter objectWriter;

    AtlasCsvMapper(Class<?> aClass) {
      CsvMapper csvMapper = createCsvMapper();
      CsvSchema csvSchema = csvMapper
          .schemaFor(aClass)
          .withHeader()
          .withColumnSeparator(';');
      this.objectWriter = csvMapper
          .writerFor(aClass)
          .with(csvSchema);
    }

    private CsvMapper createCsvMapper() {
      CsvMapper mapper = new CsvMapper();
      mapper.registerModule(new JavaTimeModule());
      mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      return mapper;
    }

  }

}
