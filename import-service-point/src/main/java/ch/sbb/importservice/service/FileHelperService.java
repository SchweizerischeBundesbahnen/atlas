package ch.sbb.importservice.service;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.importservice.service.csv.CsvFileNameModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Slf4j
@Service
public class FileHelperService {

  private final AmazonService amazonService;
  private final FileService fileService;

  public File getFileFromMultipart(MultipartFile multipartFile) {
    return fileService.getFileFromMultipart(multipartFile);
  }

  public File downloadImportFileFromS3(CsvFileNameModel csvFileNameModel) {
    try {
      return downloadImportFile(csvFileNameModel);
    } catch (IOException e) {
      throw new FileException(e);
    }
  }

  public void deleteConsumedFile(File file) {
    try {
      log.info("Delete file {} ", file.getAbsolutePath());
      Files.deleteIfExists(file.toPath());
    } catch (IOException e) {
      throw new FileException(e);
    }
  }

  private File downloadImportFile(CsvFileNameModel csvImportFile) throws IOException {
    List<String> foundImportFileKeys = amazonService.getS3ObjectKeysFromPrefix(AmazonBucket.EXPORT, csvImportFile.getS3BucketDir(),
        csvImportFile.getFileName());
    String fileKeyToDownload = handleImportFileKeysResult(foundImportFileKeys, csvImportFile.getFileName());
    log.info("Found File with name: {}", fileKeyToDownload);
    log.info("Downloading {} ...", fileKeyToDownload);
    File download = amazonService.pullFile(AmazonBucket.EXPORT, fileKeyToDownload);
    log.info("Downloaded file: " + download.getName() + ", size: " + download.length() + " bytes");
    return download;
  }

  private String handleImportFileKeysResult(List<String> importFileKeys, String csvImportFilePrefix) {
    if (importFileKeys.isEmpty()) {
      throw new FileException("[IMPORT]: File " + csvImportFilePrefix + " not found on S3");
    } else if (importFileKeys.size() > 1) {
      throw new FileException("[IMPORT]: Found more than 1 file " + csvImportFilePrefix + " to download on S3");
    }
    return importFileKeys.get(0);
  }
}
