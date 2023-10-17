package ch.sbb.importservice.service;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Slf4j
@Service
public class FileHelperService {

  public static final String SERVICE_POINT_FILE_PREFIX = "DIDOK3_DIENSTSTELLEN_ALL_V_3_";
  public static final String LOADING_POINT_FILE_PREFIX = "DIDOK3_LADESTELLEN_";
  public static final String TRAFFIC_POINT_FILE_PREFIX = "DIDOK3_VERKEHRSPUNKTELEMENTE_ALL_V_1_";
  private static final String SERVICEPOINT_DIDOK_DIR_NAME = "servicepoint_didok";

  private final AmazonService amazonService;
  private final FileService fileService;

  public File getFileFromMultipart(MultipartFile multipartFile) {
    return fileService.getFileFromMultipart(multipartFile);
  }

  public File downloadImportFileFromS3(String csvImportFilePrefix) {
    try {
      return downloadImportFile(csvImportFilePrefix);
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

  private String attachTodayDate(String csvImportFilePrefix) {
    LocalDate today = LocalDate.now();
    return csvImportFilePrefix + replaceHyphensWithUnderscores(today.toString());
  }

  private String replaceHyphensWithUnderscores(String input) {
    return input.replaceAll("-", "");
  }

  private File downloadImportFile(String csvImportFile) throws IOException {
    List<String> foundImportFileKeys = amazonService.getS3ObjectKeysFromPrefix(AmazonBucket.EXPORT, SERVICEPOINT_DIDOK_DIR_NAME,
        csvImportFile);
    String fileKeyToDownload = handleImportFileKeysResult(foundImportFileKeys, csvImportFile);
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
