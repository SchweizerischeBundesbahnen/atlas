package ch.sbb.importservice.service;

import ch.sbb.atlas.base.service.amazon.service.AmazonService;
import ch.sbb.atlas.base.service.amazon.service.FileService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

  private static final String SERVICEPOINT_DIDOK_DIR_NAME = "servicepoint_didok";
  private final AmazonService amazonService;

  private final FileService fileService;

  public File getFileFromMultipart(MultipartFile multipartFile) {
    String dir = fileService.getDir();
    String originalFileName = multipartFile.getOriginalFilename();
    File fileToImport = new File(dir + File.separator + originalFileName);
    try (OutputStream os = new FileOutputStream(fileToImport)) {
      os.write(multipartFile.getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return fileToImport;
  }

  public File downloadImportFileFromS3(String csvImportFilePrefix) throws IOException {
    return downloadImportFileWithPrefix(csvImportFilePrefix);
  }

  private String attachTodayDate(String csvImportFilePrefix) {
    LocalDate today = LocalDate.now();
    return csvImportFilePrefix + replaceHyphensWithUnderscores(today.toString());
  }

  private String replaceHyphensWithUnderscores(String input) {
    return input.replaceAll("-", "");
  }

  private File downloadImportFileWithPrefix(String csvImportFilePrefix) throws IOException {
    String csvImportFilePrefixToday = attachTodayDate(csvImportFilePrefix);
    log.info("Downloding CSV file..");
    List<String> foundImportFileKeys = amazonService.getS3ObjectKeysFromPrefix(SERVICEPOINT_DIDOK_DIR_NAME,
        csvImportFilePrefixToday);
    String fileKeyToDownload = handleImportFileKeysResult(foundImportFileKeys, csvImportFilePrefixToday);
    log.info("Found File with name: {}. Downloading...", fileKeyToDownload);
    File download = amazonService.pullFile(fileKeyToDownload);
    log.info("Downloaded file: " + download.getName() + ", size: " + download.length() + " bytes");
    return download;
  }

  private String handleImportFileKeysResult(List<String> importFileKeys, String csvImportFilePrefix) {
    if (importFileKeys.isEmpty()) {
      //TODO: create custom Exception
      throw new RuntimeException("[IMPORT]: File " + csvImportFilePrefix + " not found file on S3");
    } else if (importFileKeys.size() > 1) {
      throw new RuntimeException("[IMPORT]: Found more than 1 file " + csvImportFilePrefix + " to download on S3");
    }
    return importFileKeys.get(0);
  }
}
