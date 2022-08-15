package ch.sbb.atlas.amazon.service;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class FileServiceTest {

  private FileService fileService = new FileService();

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
    assertThat(zipFile).isNotNull();
    assertThat(zipFile.getName()).isEqualTo("tmp.csv.zip");
  }

  @Test
  public void shouldGetDirWhenActivatedProfileIsDefined() {
    //when
    fileService.setActiveProfile("dev");
    String result = fileService.getDir();
    //then
    assertThat(result).isEqualTo("/usr/local/atlas/tmp/");
  }

  @Test
  public void shouldGetDirWhenActivatedProfileIsNull() {
    //when
    String result = fileService.getDir();
    //then
    assertThat(result).isEqualTo("./export/");
  }

  @Test
  public void shouldGetDirWhenActivatedProfileIsLocal() {
    //when
    String result = fileService.getDir();
    //then
    assertThat(result).isEqualTo("./export/");
  }


}