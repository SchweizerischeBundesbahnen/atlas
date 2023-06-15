package ch.sbb.exportservice.service;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ServicePointExportType;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileExportService {

  private static final String S3_BUCKER_SERVICE_POINT_EXPORT_DIR = "service_point";
  private final AmazonService amazonService;

  private final FileService fileService;

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
    String actualDate = LocalDate.now()
        .format(DateTimeFormatter.ofPattern(
            AtlasApiConstants.DATE_FORMAT_PATTERN));
    return dir + exportType.getDir() + "-" + exportType.getFileTypePrefix() + "-service-point-" + actualDate
        + exportExtensionFileType.getExtention();
  }

}
