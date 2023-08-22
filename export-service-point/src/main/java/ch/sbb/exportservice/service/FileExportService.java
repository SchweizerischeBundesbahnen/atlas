package ch.sbb.exportservice.service;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportType;
import java.nio.file.Files;
import ch.sbb.exportservice.model.SpExportFileName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class FileExportService {

  private static final int OUT_BUFFER = 4096;
  private static final int IN_BUFFER = 1024;
  private final AmazonService amazonService;

  private final FileService fileService;

  public StreamingResponseBody streamJsonFile(ExportType exportType, SpExportFileName exportFileName) {
    return fileService.streamingJsonFile(exportType, exportFileName, amazonService, null, getBaseFileName(exportType, exportFileName));
  }

//  public StreamingResponseBody streamingJsonFile(ExportType exportType, SpExportFileName exportFileName) {
//    String fileToDownload = getJsonFileToDownload(exportType,exportFileName);
//    try {
//      File file = amazonService.pullFile(AmazonBucket.EXPORT, fileToDownload);
//      byte[] bytes = decompressGzipToBytes(file.toPath());
//      InputStream inputStream = new ByteArrayInputStream(bytes);
//      return writeOutputStream(file, inputStream);
//    } catch (IOException e) {
//      throw new FileException(e);
//    }
//  }

  public StreamingResponseBody streamGzipFile(ExportType exportType, SpExportFileName exportFileName) {
    return fileService.streamingGzipFile(exportType, exportFileName, amazonService, null, getBaseFileName(exportType, exportFileName));
  }

//  public StreamingResponseBody streamingGzipFile(ExportType exportType, SpExportFileName exportFileName) {
//    String fileToDownload = getJsonFileToDownload(exportType, exportFileName);
//    try {
//      File file = amazonService.pullFile(AmazonBucket.EXPORT, fileToDownload);
//      InputStream inputStream = new FileInputStream(file);
//      return writeOutputStream(file, inputStream);
//    } catch (IOException e) {
//      throw new FileException(e);
//    }
//  }

//  private StreamingResponseBody writeOutputStream(File file, InputStream inputStream) {
//    return outputStream -> {
//      int len;
//      byte[] data = new byte[OUT_BUFFER];
//      while ((len = inputStream.read(data, 0, data.length)) != -1) {
//        outputStream.write(data, 0, len);
//      }
//      inputStream.close();
//      file.delete();
//    };
//  }
//
//  private String getJsonFileToDownload(ExportType exportType, SpExportFileName exportFileName) {
//    return exportFileName.getBaseDir()
//        + "/"
//        + exportType.getDir()
//        + "/"
//        + getBaseFileName(exportType, exportFileName)
//        + ".json.gz";
//  }

//  byte[] decompressGzipToBytes(Path source) throws IOException {
//    ByteArrayOutputStream output = new ByteArrayOutputStream();
//    try (GZIPInputStream gis = new GZIPInputStream(
//        new FileInputStream(source.toFile()))) {
//      byte[] buffer = new byte[IN_BUFFER];
//      int len;
//      while ((len = gis.read(buffer)) > 0) {
//        output.write(buffer, 0, len);
//      }
//    }
//    return output.toByteArray();
//  }

  public void exportFile(File file, ExportType exportType, SpExportFileName exportFileName, ExportExtensionFileType exportExtensionFileType) {
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

  public String createFileNamePath(ExportExtensionFileType exportExtensionFileType, ExportType exportType, SpExportFileName exportFileName) {
    String dir = fileService.getDir();
    String baseFileName = getBaseFileName(exportType,exportFileName);
    return dir + baseFileName + exportExtensionFileType.getExtention();
  }

  public String getBaseFileName(ExportType exportType, SpExportFileName exportFileName) {
    String actualDate = LocalDate.now()
        .format(DateTimeFormatter.ofPattern(
            AtlasApiConstants.DATE_FORMAT_PATTERN));
    return exportType.getDir() + "-" + exportType.getFileTypePrefix() + "-"+exportFileName.getFileName()+"-" + actualDate;
  }

}
