package ch.sbb.exportservice.service;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonFileStreamingService;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.BatchExportFileName;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.model.SePoDiExportType;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Service
@RequiredArgsConstructor
public class FileExportService<T extends ExportTypeBase> {

  private static final String JSON_GZ_EXTENSION = ".json.gz";
  public static final String S3_BUCKET_PATH_SEPARATOR = "/";

  private final AmazonFileStreamingService amazonFileStreamingService;
  private final AmazonService amazonService;
  private final FileService fileService;

  public StreamingResponseBody streamJsonFile(T exportTypeBase, BatchExportFileName exportFileName) {
    String fileToStream = getFileToStream(exportTypeBase, exportFileName, JSON_GZ_EXTENSION);
    return amazonFileStreamingService.streamFileAndDecompress(AmazonBucket.EXPORT, fileToStream);
  }

  public StreamingResponseBody streamGzipFile(T exportTypeBase, BatchExportFileName exportFileName) {
    String fileToStream = getFileToStream(exportTypeBase, exportFileName, JSON_GZ_EXTENSION);
    return amazonFileStreamingService.streamFile(AmazonBucket.EXPORT, fileToStream);
  }

  public StreamingResponseBody streamLatestGzipFile(String fileToStream) {
    return amazonFileStreamingService.streamFile(AmazonBucket.EXPORT, fileToStream);
  }


  public StreamingResponseBody streamLatestJsonFile(String fileToStream) {
    return amazonFileStreamingService.streamFileAndDecompress(AmazonBucket.EXPORT, fileToStream);
  }

  private String getFileToStream(T sePoDiExportType, BatchExportFileName exportFileName, String extension) {
    return exportFileName.getBaseDir() + "/" +
        sePoDiExportType.getDir() + "/" +
        getBaseFileName(sePoDiExportType, exportFileName) + extension;
  }

  public void exportFile(File file, T exportType, BatchExportFileName exportFileName,
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

  public String createFileNamePath(ExportExtensionFileType exportExtensionFileType, T exportType,
      BatchExportFileName exportFileName) {
    String dir = fileService.getDir();
    String baseFileName = getBaseFileName(exportType, exportFileName);
    return dir + baseFileName + exportExtensionFileType.getExtension();
  }

  public String getBaseFileName(T exportType, BatchExportFileName exportFileName) {
    if (exportType instanceof PrmExportType) {
      String actualDate = LocalDate.now().format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
      return exportType.getDir() + "_" + exportFileName.getFileName() + "-" + actualDate;
    }
    if (exportType instanceof SePoDiExportType) {
      String actualDate = LocalDate.now().format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
      return exportType.getDir() + "-" + exportType.getFileTypePrefix() + "-" + exportFileName.getFileName() + "-" + actualDate;
    }
    throw new IllegalArgumentException("The Given value is not allowed!");
  }

  public String getLatestUploadedFileName(String filePathPrefix, String fileName) {
    return amazonService.getLatestJsonUploadedObject(AmazonBucket.EXPORT, filePathPrefix, fileName);
  }

}
