package ch.sbb.atlas.amazon.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FileService {

  public static final int BUFFER_SIZE = 4096;
  public static final int ZERO = 0;
  public static final String ZIP = ".zip";
  private static final String DOCKER_FILE_DIRECTORY = "/usr/local/atlas/tmp/";

  @Value("${spring.profiles.active:local}")
  private String activeProfile;

  public File zipFile(File file, String filename) {
    File zipFile = new File(filename + ZIP);

    try (ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFile));
        InputStream inputStream = new FileInputStream(file)) {

      ZipEntry entry = new ZipEntry(filename);
      zipStream.putNextEntry(entry);

      copyStream(inputStream, zipStream);
      zipStream.flush();

    } catch (Exception e) {
      log.error("Error during write ZipFile", e);
    }
    if (!zipFile.canRead()) {
      zipFile.setReadable(true);
    }
    return zipFile;
  }

  private void copyStream(InputStream in, OutputStream out) throws IOException {
    byte[] buf = new byte[BUFFER_SIZE];
    int len;
    while ((len = in.read(buf)) >= ZERO) {
      out.write(buf, ZERO, len);
    }
    out.flush();
  }

  public String getDir() {
    if ("local".equals(activeProfile)) {
      return "";
    }
    return DOCKER_FILE_DIRECTORY;
  }

}
