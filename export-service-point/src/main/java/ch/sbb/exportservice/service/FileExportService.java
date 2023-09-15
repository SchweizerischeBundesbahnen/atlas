package ch.sbb.exportservice.service;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonFileStreamingService;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.exportservice.model.BatchExportFileName;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportType;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Service
@RequiredArgsConstructor
public class FileExportService {

  private final AmazonFileStreamingService amazonFileStreamingService;
  private final AmazonService amazonService;
  private final FileService fileService;

  public StreamingResponseBody streamJsonFile(ExportType exportType, BatchExportFileName exportFileName) {
    String fileToStream = getFileToStream(exportType, exportFileName);
    return amazonFileStreamingService.streamFileAndDecompress(AmazonBucket.EXPORT, fileToStream);
  }

  public StreamingResponseBody streamGzipFile(ExportType exportType, BatchExportFileName exportFileName) {
    String fileToStream = getFileToStream(exportType, exportFileName);
    return amazonFileStreamingService.streamFile(AmazonBucket.EXPORT, fileToStream);
  }

  private String getFileToStream(ExportType exportType, BatchExportFileName exportFileName) {
    return exportFileName.getBaseDir() + "/" +
        exportType.getDir() + "/" +
        getBaseFileName(exportType, exportFileName)+
        ".json.gz";
  }

  public void exportFile(File file, ExportType exportType, BatchExportFileName exportFileName,
      ExportExtensionFileType exportExtensionFileType) {
    String pathDirectory = exportFileName.getBaseDir() + "/" + exportType.getDir();
    try {
      if (exportExtensionFileType.equals(ExportExtensionFileType.CSV_EXTENSION)) {
        amazonService.putZipFile(AmazonBucket.EXPORT, file, pathDirectory);
        return;
      }
      if (exportExtensionFileType.equals(ExportExtensionFileType.JSON_EXTENSION)) {
        amazonService.putGzipFile(AmazonBucket.EXPORT, file, pathDirectory);
        return;
      }
      throw new IllegalStateException("File extension must me " + ExportExtensionFileType.CSV_EXTENSION.name() + " or " +
          ExportExtensionFileType.JSON_EXTENSION.name());
    } catch (IOException e) {
      throw new FileException("Error uploading file: " + file.getName() + " to bucket: " + AmazonBucket.EXPORT, e);
    }
  }

  public String createFileNamePath(ExportExtensionFileType exportExtensionFileType, ExportType exportType,
      BatchExportFileName exportFileName) {
    String dir = fileService.getDir();
    String baseFileName = getBaseFileName(exportType, exportFileName);
    return dir + baseFileName + exportExtensionFileType.getExtension();
  }

  public String getBaseFileName(ExportType exportType, BatchExportFileName exportFileName) {
    String actualDate = LocalDate.now().format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
    return exportType.getDir() + "-" + exportType.getFileTypePrefix() + "-" + exportFileName.getFileName() + "-" + actualDate;
  }

}
