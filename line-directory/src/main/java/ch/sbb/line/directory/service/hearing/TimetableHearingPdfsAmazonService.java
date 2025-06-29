package ch.sbb.line.directory.service.hearing;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import java.io.File;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimetableHearingPdfsAmazonService {

  private final AmazonService amazonService;

  public void uploadPdfFiles(List<File> files, String dirName) {
    log.info("Starting upload to S3 Bucket");
    files.forEach(file -> amazonService.putFile(AmazonBucket.HEARING_DOCUMENT, file, dirName));
    log.info("Upload complete.");
  }

  public File downloadPdfFile(String dirName, String fileName) {
    return amazonService.pullFile(AmazonBucket.HEARING_DOCUMENT, dirName + "/" + fileName);
  }

  public void deletePdfFile(String dirName, String fileName) {
    amazonService.deleteFile(AmazonBucket.HEARING_DOCUMENT, dirName + "/" + fileName);
  }

}
