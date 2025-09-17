package ch.sbb.line.directory.module.tth.service;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// todo: test Paths.get
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
    return amazonService.pullFile(AmazonBucket.HEARING_DOCUMENT, Paths.get(dirName, fileName).toString());
  }

  public void deletePdfFile(String dirName, String fileName) {
    amazonService.deleteFile(AmazonBucket.HEARING_DOCUMENT, Paths.get(dirName, fileName).toString());
  }

}
