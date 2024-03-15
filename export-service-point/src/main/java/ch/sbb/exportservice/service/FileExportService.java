package ch.sbb.exportservice.service;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonFileStreamingService;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.export.enumeration.ExportFileName;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePath;
import java.time.Clock;
import java.time.LocalDate;
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
  private final Clock clock;

  public ExportFilePath createExportFilePath(ExportTypeBase exportTypeBase, ExportFileName exportFileName,
      ExportExtensionFileType exportExtensionFileType) {
    return ExportFilePath.builder()
        .baseDir(exportFileName.getBaseDir())
        .dir(exportTypeBase.getDir())
        .prefix(exportTypeBase.getFileTypePrefix())
        .fileName(exportFileName.getFileName())
        .extension(exportExtensionFileType == null ? null : exportExtensionFileType.getExtension())
        .systemDir(fileService.getDir())
        .actualDate(LocalDate.now(clock))
        .build();
  }

  public ExportFilePath createExportFilePath(ExportTypeBase exportTypeBase, ExportFileName exportFileName) {
    return createExportFilePath(exportTypeBase, exportFileName, null);
  }

  public InputStreamResource streamJsonFile(String fileName) {
    logStreamingStart(fileName);
    return amazonFileStreamingService.streamFileAndDecompress(AmazonBucket.EXPORT, fileName);
  }

  public InputStreamResource streamGzipFile(String fileName) {
    logStreamingStart(fileName);
    return amazonFileStreamingService.streamFile(AmazonBucket.EXPORT, fileName);
  }

  public String getLatestUploadedFileName(ExportFilePath exportFilePath) {
    return amazonService.getLatestJsonUploadedObject(AmazonBucket.EXPORT, exportFilePath.s3BucketDirPath(),
        exportFilePath.getPrefix());
  }

  private static void logStreamingStart(String fileName) {
    log.info("Start streaming file: " + fileName);
  }
}
