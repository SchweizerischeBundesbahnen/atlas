package ch.sbb.exportservice.service;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonFileStreamingService;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.exportservice.model.ExportFilePathV1;
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

  public InputStreamResource streamJsonFile(ExportFilePathV1 exportFilePathV1) {
    logStreamingStart(exportFilePathV1.fileToStream());
    return amazonFileStreamingService.streamFileAndDecompress(AmazonBucket.EXPORT, exportFilePathV1.fileToStream());
  }

  public InputStreamResource streamLatestJsonFile(ExportFilePathV1 exportFilePathV1) {
    String latestUploadedFileName = getLatestUploadedFileName(exportFilePathV1);
    logStreamingStart(latestUploadedFileName);
    return amazonFileStreamingService.streamFileAndDecompress(AmazonBucket.EXPORT, latestUploadedFileName);
  }

  public InputStreamResource streamGzipFile(String fileName) {
    logStreamingStart(fileName);
    return amazonFileStreamingService.streamFile(AmazonBucket.EXPORT, fileName);
  }

  public String getLatestUploadedFileName(ExportFilePathV1 exportFilePathV1) {
    return amazonService.getLatestJsonUploadedObject(AmazonBucket.EXPORT, exportFilePathV1.s3BucketDirPath(),
        exportFilePathV1.getPrefix());
  }

  private static void logStreamingStart(String fileName) {
    log.info("Start streaming file: " + fileName);
  }
}
