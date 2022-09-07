package ch.sbb.atlas.base.service.amazon.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.Setter;
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
  @Setter
  private String activeProfile;

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

  public String getDir() {
    if ("local".equals(activeProfile) || activeProfile == null) {
      File dir = new File("./export");
      if (!dir.exists()) {
        dir.mkdirs();
      }
      return "./export/";
    }
    return DOCKER_FILE_DIRECTORY;
  }

}
