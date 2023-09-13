package ch.sbb.atlas.amazon.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class FileServiceImplTest {

  private static final String SEPARATOR = File.separator;

  private final FileServiceImpl fileService = new FileServiceImpl();

  @Test
  public void shouldCreateZipFile() throws IOException {
    //given
    File dir = new File("./export");
    if (!dir.exists()) {
      dir.mkdirs();
    }
    dir.deleteOnExit();
    Path tempFile = Files.createFile(Paths.get("./export/tmp.csv"));
    tempFile.toFile().deleteOnExit();

    //when
    File zipFile = fileService.zipFile(tempFile.toFile());
    zipFile.deleteOnExit();

    //then
    assertThat(zipFile).isNotNull().hasName("tmp.csv.zip");
  }

  @Test
  public void shouldGetDirWhenActivatedProfileIsDefined() {
    //given
    fileService.setActiveProfile("dev");
    //when
    String result = fileService.getDir();
    //then
    assertThat(result).isEqualTo("/usr/local/atlas/tmp/");
  }

  @Test
  public void shouldGetDirWhenActivatedProfileIsNull() {
    //given
    fileService.setActiveProfile(null);
    //when
    String result = fileService.getDir();
    //then
    assertThat(result).isEqualTo("." + SEPARATOR + "export" + SEPARATOR);
  }

  @Test
  public void shouldGetDirWhenActivatedProfileIsLocal() {
    //given
    fileService.setActiveProfile("local");
    //when
    String result = fileService.getDir();
    //then
    assertThat(result).isEqualTo("." + SEPARATOR + "export" + SEPARATOR);
  }

}
