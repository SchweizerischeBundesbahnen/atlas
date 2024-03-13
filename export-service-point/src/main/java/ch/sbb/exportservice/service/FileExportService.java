package ch.sbb.exportservice.service;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonFileStreamingService;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.export.enumeration.ExportFileName;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileExportService {

  private final AmazonFileStreamingService amazonFileStreamingService;
  private final AmazonService amazonService;
  private final FileService fileService;

  public FilePath createFilePath(ExportTypeBase exportTypeBase, ExportFileName exportFileName,
      ExportExtensionFileType exportExtensionFileType) {
    FilePath filePath = new FilePath();
    filePath.baseDir = exportFileName.getBaseDir();
    filePath.dir = exportTypeBase.getDir();
    filePath.prefix = exportTypeBase.getFileTypePrefix();
    filePath.fileName = exportFileName.getFileName();
    filePath.extension = exportExtensionFileType == null ? null : exportExtensionFileType.getExtension();
    return filePath;
  }

  public FilePath createFilePath(ExportTypeBase exportTypeBase, ExportFileName exportFileName) {
    return createFilePath(exportTypeBase, exportFileName, null);
  }

  public class FilePath {

    private String baseDir;
    private String dir;
    private String prefix;
    private String fileName;
    private String extension;

    public String actualDateFileName() {
      String actualDate = LocalDate.now().format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
      if (prefix == null || prefix.isEmpty()) {
        return dir + "-" + fileName + "-" + actualDate;
      }
      return dir + "-" + prefix + "-" + fileName + "-" + actualDate;
    }

    public String getFileToStream() {
      return baseDir + "/" + dir + "/" + actualDateFileName() + ".json.gz";
    }

    public String actualDateFilePath() {
      return fileService.getDir() + actualDateFileName() + extension;
    }

    public String s3BucketDirPath() {
      return baseDir + "/" + dir;
    }
  }

  public InputStreamResource streamJsonFile(String fileName) {
    logStreamingStart(fileName);
    return amazonFileStreamingService.streamFileAndDecompress(AmazonBucket.EXPORT, fileName);
  }

  public InputStreamResource streamGzipFile(String fileName) {
    logStreamingStart(fileName);
    return amazonFileStreamingService.streamFile(AmazonBucket.EXPORT, fileName);
  }

  public String getLatestUploadedFileName(FilePath filePath) {
    return amazonService.getLatestJsonUploadedObject(AmazonBucket.EXPORT, filePath.s3BucketDirPath(), filePath.prefix);
  }

  private static void logStreamingStart(String fileName) {
    log.info("Start streaming file: " + fileName);
  }
}
