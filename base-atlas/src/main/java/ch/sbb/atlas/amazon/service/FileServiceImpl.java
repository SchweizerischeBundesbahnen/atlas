package ch.sbb.atlas.amazon.service;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.export.enumeration.ExportFileName;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class FileServiceImpl implements FileService {

  public static final String ZIP = ".zip";
  private static final String DOCKER_FILE_DIRECTORY = "/usr/local/atlas/tmp/";

  private static final int OUT_BUFFER = 4096;
  private static final int IN_BUFFER = 1024;

  @Value("${spring.profiles.active:local}")
  @Setter
  private String activeProfile;

  @Override
  public File zipFile(File file) {
    String filename = file.getName();
    File zipFile = new File(file.toPath().getParent() + "/" + file.getName() + ZIP);

    try (ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFile));
        InputStream inputStream = new FileInputStream(file)) {

      ZipEntry entry = new ZipEntry(filename);
      zipStream.putNextEntry(entry);
      inputStream.transferTo(zipStream);
      zipStream.flush();

    } catch (Exception e) {
      log.error("Error during write ZipFile", e);
    }
    if (!zipFile.canRead()) {
      zipFile.setReadable(true);
    }
    return zipFile;
  }

  @Override
  public String getDir() {
    log.info("Getting Directory for activeProfile={}", activeProfile);
    if ("local".equals(activeProfile) || activeProfile == null) {
      String pathnameExportDir = "." + File.separator + "export" + File.separator;
      File dir = new File(pathnameExportDir);
      if (!dir.exists()) {
        dir.mkdirs();
      }
      return pathnameExportDir;
    }
    return DOCKER_FILE_DIRECTORY;
  }

  @Override
  public boolean clearDir() {
    Optional<File[]> filesInDir = Optional.ofNullable(new File(getDir()).listFiles());
    if (filesInDir.isEmpty()) {
      return true;
    }
    Set<Boolean> deletionResults = Arrays.stream(filesInDir.get()).map(File::delete).collect(Collectors.toSet());
    return deletionResults.size() == 1 && deletionResults.contains(true);
  }

  @Override
  public File getFileFromMultipart(MultipartFile multipartFile) {
    String dir = getDir();
    String originalFileName = multipartFile.getOriginalFilename();
    File fileToImport = new File(dir + File.separator + originalFileName);
    try (OutputStream os = new FileOutputStream(fileToImport)) {
      os.write(multipartFile.getBytes());
    } catch (IOException e) {
      throw new FileException(e);
    }
    return fileToImport;
  }

  @Override
  public StreamingResponseBody streamingJsonFile(ExportTypeBase exportType, ExportFileName exportFileName,
      AmazonService amazonService, String fileName) {
    String fileToDownload = getJsonFileToDownload(exportType, exportFileName, fileName);
    try {
      File file = amazonService.pullFile(AmazonBucket.EXPORT, fileToDownload);
      byte[] bytes = decompressGzipToBytes(file.toPath());
      InputStream inputStream = new ByteArrayInputStream(bytes);
      return writeOutputStream(file, inputStream);
    } catch (IOException e) {
      throw new FileException(e);
    }
  }

  @Override
  public StreamingResponseBody streamingGzipFile(ExportTypeBase exportType, ExportFileName exportFileName,
      AmazonService amazonService, String fileName) {
    String fileToDownload = getJsonFileToDownload(exportType, exportFileName, fileName);
    try {
      File file = amazonService.pullFile(AmazonBucket.EXPORT, fileToDownload);
      InputStream inputStream = new FileInputStream(file);
      return writeOutputStream(file, inputStream);
    } catch (IOException e) {
      throw new FileException(e);
    }
  }

  private StreamingResponseBody writeOutputStream(File file, InputStream inputStream) {
    return outputStream -> {
      int len;
      byte[] data = new byte[OUT_BUFFER];
      while ((len = inputStream.read(data, 0, data.length)) != -1) {
        outputStream.write(data, 0, len);
      }
      inputStream.close();
      Files.delete(file.toPath());
    };
  }

  byte[] decompressGzipToBytes(Path source) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try (GZIPInputStream gis = new GZIPInputStream(
        new FileInputStream(source.toFile()))) {
      byte[] buffer = new byte[IN_BUFFER];
      int len;
      while ((len = gis.read(buffer)) > 0) {
        output.write(buffer, 0, len);
      }
    }
    return output.toByteArray();
  }

  private String getJsonFileToDownload(ExportTypeBase exportType, ExportFileName exportFileName, String fileName) {
    String fileNameSuffix = "/" + fileName + ".json.gz";
    String jsonFileName = exportFileName.getBaseDir();
    if (!exportFileName.toString().equals("BUSINESS_ORGANISATION_VERSION")) {
      jsonFileName += "/" + exportType.getDir();
    }
    jsonFileName += fileNameSuffix;
    return jsonFileName;
  }

}
