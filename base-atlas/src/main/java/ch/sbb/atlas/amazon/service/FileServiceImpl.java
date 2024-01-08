package ch.sbb.atlas.amazon.service;

import ch.sbb.atlas.amazon.exception.FileException;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
public class FileServiceImpl implements FileService {

  public static final String ZIP = ".zip";
  private static final String DOCKER_FILE_DIRECTORY = "/usr/local/atlas/tmp/";

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
      boolean succeeded = zipFile.setReadable(true);
      if (!succeeded) {
        log.error("Could not set file to readable");
      }
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
  public StreamingResponseBody toStreamingResponse(File fileToCleanUp, InputStream inputStream) {
    return outputStream -> {
      inputStream.transferTo(outputStream);
      inputStream.close();
      Files.delete(fileToCleanUp.toPath());
    };
  }

  @Override
  public byte[] gzipDecompress(File file) {
    try {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      try (GZIPInputStream gis = new GZIPInputStream(new FileInputStream(file))) {
        gis.transferTo(output);
      }
      return output.toByteArray();
    } catch (IOException exception) {
      throw new IllegalStateException("Could not unzip file", exception);
    }
  }

  @Override
  public byte[] gzipCompress(byte[] bytes) throws IOException {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream out = new GZIPOutputStream(baos)) {
      out.write(bytes, 0, bytes.length);
      out.finish();

      return baos.toByteArray();
    }
  }

  @Override
  public byte[] gzipDecompress(S3ObjectInputStream s3ObjectInputStream) {
    try {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      try (GZIPInputStream gis = new GZIPInputStream(s3ObjectInputStream)) {
        gis.transferTo(output);
      }
      return output.toByteArray();
    } catch (IOException exception) {
      throw new IllegalStateException("Could not unzip file", exception);
    }
  }
}
