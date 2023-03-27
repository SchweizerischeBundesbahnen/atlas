package ch.sbb.line.directory.service.hearing;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimetableHearingPdfsAmazonService {

  private final AmazonService amazonService;

  public void uploadPdfFiles(List<File> files, String dirName) {
    files.forEach(file -> {
      try {
        amazonService.putFile(AmazonBucket.HEARING_DOCUMENT, file, dirName);
      } catch (IOException e) {
        throw new FileException("Error uploading file: " + file.getName() + " to bucket: " + AmazonBucket.HEARING_DOCUMENT, e);
      }
    });
  }

  public File downloadPdfFile(String dirName, String fileName) {
    try {
      return amazonService.pullFile(AmazonBucket.HEARING_DOCUMENT, dirName + "/" + fileName);
    } catch (IOException e) {
      throw new FileException("Error downloading file: " + fileName + " to bucket: " + AmazonBucket.HEARING_DOCUMENT, e);
    }
  }

  public void deletePdfFile(String dirName, String fileName) {
    amazonService.deleteFile(AmazonBucket.HEARING_DOCUMENT, dirName + "/" + fileName);
  }

}
