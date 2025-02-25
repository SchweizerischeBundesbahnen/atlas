package ch.sbb.exportservice.service;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonFileStreamingService;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.exportservice.model.ExportFilePath;
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

  public InputStreamResource streamJsonFile(ExportFilePath exportFilePath) {
    logStreamingStart(exportFilePath.fileToStream());
    return amazonFileStreamingService.streamFileAndDecompress(AmazonBucket.EXPORT, exportFilePath.fileToStream());
  }

  public InputStreamResource streamLatestJsonFile(ExportFilePath exportFilePath) {
    String latestUploadedFileName = getLatestUploadedFileName(exportFilePath);
    logStreamingStart(latestUploadedFileName);
    return amazonFileStreamingService.streamFileAndDecompress(AmazonBucket.EXPORT, latestUploadedFileName);
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
