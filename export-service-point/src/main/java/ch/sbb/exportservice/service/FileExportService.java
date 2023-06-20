package ch.sbb.exportservice.service;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ServicePointExportType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.zip.GZIPInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Service
@RequiredArgsConstructor
public class FileExportService {

  private static final String S3_BUCKER_SERVICE_POINT_EXPORT_DIR = "service_point";
  private static final int OUT_BUFFER = 4096;
  private static final int IN_BUFFER = 1024;
  private final AmazonService amazonService;

  private final FileService fileService;

  public StreamingResponseBody streamingJsonFile(ServicePointExportType servicePointExportType) {
    String fileToDownload = getJsonFileToDownload(servicePointExportType);
    try {
      File file = amazonService.pullFile(AmazonBucket.EXPORT, fileToDownload);
      byte[] bytes = decompressGzipToBytes(file.toPath());
      InputStream inputStream = new ByteArrayInputStream(bytes);
      return writeOutputStream(file, inputStream);
    } catch (IOException e) {
      throw new FileException(e);
    }
  }

  public StreamingResponseBody streamingGzipFile(ServicePointExportType servicePointExportType) {
    String fileToDownload = getJsonFileToDownload(servicePointExportType);
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

  private String getJsonFileToDownload(ServicePointExportType servicePointExportType) {
    return S3_BUCKER_SERVICE_POINT_EXPORT_DIR
        + "/"
        + servicePointExportType.getDir()
        + "/"
        + getBaseFileName(servicePointExportType)
        + ".json.gzip";
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

  public URL exportFile(File file, ServicePointExportType exportType, ExportExtensionFileType exportExtensionFileType) {
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

  public String createFileNamePath(ExportExtensionFileType exportExtensionFileType, ServicePointExportType exportType) {
    String dir = fileService.getDir();
    String baseFileName = getBaseFileName(exportType);
    return dir + baseFileName + exportExtensionFileType.getExtention();
  }

  public String getBaseFileName(ServicePointExportType exportType) {
    String actualDate = LocalDate.now()
        .format(DateTimeFormatter.ofPattern(
            AtlasApiConstants.DATE_FORMAT_PATTERN));
    return exportType.getDir() + "-" + exportType.getFileTypePrefix() + "-service-point-" + actualDate;
  }

}
