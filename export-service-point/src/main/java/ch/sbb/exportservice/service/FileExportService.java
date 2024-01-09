package ch.sbb.exportservice.service;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonFileStreamingService;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.export.enumeration.ExportFileName;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.model.SePoDiExportType;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileExportService<T extends ExportTypeBase> {

  private static final String JSON_GZ_EXTENSION = ".json.gz";
  public static final String S3_BUCKET_PATH_SEPARATOR = "/";
  public static final String DOWNLOADING_FILE_INFO_MSG = "Downloading file: ";

  private final AmazonFileStreamingService amazonFileStreamingService;
  private final AmazonService amazonService;
  private final FileService fileService;

  public InputStreamResource streamJsonFile(T exportTypeBase, ExportFileName exportFileName) {
    String fileToStream = getFileToStream(exportTypeBase, exportFileName, JSON_GZ_EXTENSION);
    log.info(DOWNLOADING_FILE_INFO_MSG + fileToStream);
    return amazonFileStreamingService.streamFileAndDecompress(AmazonBucket.EXPORT, fileToStream);
  }

  public InputStreamResource streamGzipFile(T exportTypeBase, ExportFileName exportFileName) {
    String fileToStream = getFileToStream(exportTypeBase, exportFileName, JSON_GZ_EXTENSION);
    log.info(DOWNLOADING_FILE_INFO_MSG + fileToStream);
    return amazonFileStreamingService.streamFile(AmazonBucket.EXPORT, fileToStream);
  }

  public InputStreamResource streamLatestGzipFile(String fileToStream) {
    log.info(DOWNLOADING_FILE_INFO_MSG + fileToStream);
    return amazonFileStreamingService.streamFile(AmazonBucket.EXPORT, fileToStream);
  }

  public InputStreamResource streamLatestJsonFile(String fileToStream) {
    log.info(DOWNLOADING_FILE_INFO_MSG + fileToStream);
    return amazonFileStreamingService.streamFileAndDecompress(AmazonBucket.EXPORT, fileToStream);
  }

  private String getFileToStream(T sePoDiExportType, ExportFileName exportFileName, String extension) {
    return exportFileName.getBaseDir() + "/" +
        sePoDiExportType.getDir() + "/" +
        getBaseFileName(sePoDiExportType, exportFileName) + extension;
  }

  public void exportFile(File file, T exportType, ExportFileName exportFileName,
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
      ExportFileName exportFileName) {
    String dir = fileService.getDir();
    String baseFileName = getBaseFileName(exportType, exportFileName);
    return dir + baseFileName + exportExtensionFileType.getExtension();
  }

  public String getBaseFileName(T exportType, ExportFileName exportFileName) {
    if (exportType instanceof PrmExportType) {
      String actualDate = LocalDate.now().format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
      return exportType.getDir() + "-" + exportFileName.getFileName() + "-" + actualDate;
    }
    if (exportType instanceof SePoDiExportType) {
      String actualDate = LocalDate.now().format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
      return exportType.getDir() + "-" + exportType.getFileTypePrefix() + "-" + exportFileName.getFileName() + "-" + actualDate;
    }
    throw new IllegalArgumentException("The Given value is not allowed!");
  }

  public String getLatestUploadedFileName(ExportFileName exportFileName, ExportTypeBase exportTypeBase) {
    String dirPathPrefix = s3BucketDirPathPrefix(exportFileName, exportTypeBase);
    return amazonService.getLatestJsonUploadedObject(AmazonBucket.EXPORT, dirPathPrefix, exportTypeBase.getFileTypePrefix());
  }

  private static String s3BucketDirPathPrefix(ExportFileName exportFileName, ExportTypeBase exportTypeBase) {
    return exportFileName.getBaseDir() + S3_BUCKET_PATH_SEPARATOR + exportTypeBase.getDir();
  }

}
