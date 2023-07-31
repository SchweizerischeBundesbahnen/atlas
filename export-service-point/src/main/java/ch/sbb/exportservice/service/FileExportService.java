package ch.sbb.exportservice.service;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.zip.GZIPInputStream;

@Service
@RequiredArgsConstructor
public class FileExportService {

  private static final String S3_BUCKER_SERVICE_POINT_EXPORT_DIR = "service_point";
  private static final int OUT_BUFFER = 4096;
  private static final int IN_BUFFER = 1024;
  private final AmazonService amazonService;

  private final FileService fileService;

  public StreamingResponseBody streamingJsonFile(ExportType exportType) {
    String fileToDownload = getJsonFileToDownload(exportType);
    try {
      File file = amazonService.pullFile(AmazonBucket.EXPORT, fileToDownload);
      byte[] bytes = decompressGzipToBytes(file.toPath());
      InputStream inputStream = new ByteArrayInputStream(bytes);
      return writeOutputStream(file, inputStream);
    } catch (IOException e) {
      throw new FileException(e);
    }
  }

  public StreamingResponseBody streamingGzipFile(ExportType exportType) {
    String fileToDownload = getJsonFileToDownload(exportType);
    try {
      File file = amazonService.pullFile(AmazonBucket.EXPORT, fileToDownload);
      InputStream inputStream = new FileInputStream(file);
      return writeOutputStream(file, inputStream);
    } catch (IOException e) {
      throw new FileException(e);
    }
  }

  private StreamingResponseBody writeOutputStream(File file, InputStream inputStream) {
    return outputStream -> {
      int len;
      byte[] data = new byte[OUT_BUFFER];
      while ((len = inputStream.read(data, 0, data.length)) != -1) {
        outputStream.write(data, 0, len);
      }
      inputStream.close();
      file.delete();
    };
  }

  private String getJsonFileToDownload(ExportType exportType) {
    return S3_BUCKER_SERVICE_POINT_EXPORT_DIR
        + "/"
        + exportType.getDir()
        + "/"
        + getBaseFileName(exportType)
        + ".json.gz";
  }

  byte[] decompressGzipToBytes(Path source) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try (GZIPInputStream gis = new GZIPInputStream(
        new FileInputStream(source.toFile()))) {
      byte[] buffer = new byte[IN_BUFFER];
      int len;
      while ((len = gis.read(buffer)) > 0) {
        output.write(buffer, 0, len);
      }
    }
    return output.toByteArray();
  }

  public URL exportFile(File file, ExportType exportType, ExportExtensionFileType exportExtensionFileType) {
    String pathDirectory = S3_BUCKER_SERVICE_POINT_EXPORT_DIR + "/" + exportType.getDir();
    try {
      if (exportExtensionFileType.equals(ExportExtensionFileType.CSV_EXTENSION)) {
        return amazonService.putZipFile(AmazonBucket.EXPORT, file, pathDirectory);
      }
      if (exportExtensionFileType.equals(ExportExtensionFileType.JSON_EXTENSION)) {
        return amazonService.putGzipFile(AmazonBucket.EXPORT, file, pathDirectory);
      }
      throw new IllegalStateException("File extension must me " + ExportExtensionFileType.CSV_EXTENSION.name() + " or " +
          ExportExtensionFileType.JSON_EXTENSION.name());
    } catch (IOException e) {
      throw new FileException("Error uploading file: " + file.getName() + " to bucket: " + AmazonBucket.EXPORT, e);
    }
  }

  public String createFileNamePath(ExportExtensionFileType exportExtensionFileType, ExportType exportType) {
    String dir = fileService.getDir();
    String baseFileName = getBaseFileName(exportType);
    return dir + baseFileName + exportExtensionFileType.getExtention();
  }

  public String getBaseFileName(ExportType exportType) {
    String actualDate = LocalDate.now()
        .format(DateTimeFormatter.ofPattern(
            AtlasApiConstants.DATE_FORMAT_PATTERN));
    return exportType.getDir() + "-" + exportType.getFileTypePrefix() + "-service-point-" + actualDate;
  }

}
