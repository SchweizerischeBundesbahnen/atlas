package ch.sbb.atlas.amazon.service;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public class FileServiceTest {

  private FileService fileService = new FileService();

  @Test
  public void shouldCreateZipFile() throws IOException {
    //given
    Path tempFile = Files.createTempFile("tmp", ".csv");

    //when
    File zipFile = fileService.zipFile(tempFile.toFile(), "tmp.csv");

    //then
    assertThat(zipFile).isNotNull();
    assertThat(zipFile.getName()).isEqualTo("tmp.csv.zip");
    String contentType = Files.probeContentType(zipFile.toPath());
    assertThat(contentType).isEqualTo("application/x-zip-compressed");

  }

  @Test
  public void shouldGetDirWhenNoProfileIsActivated() {
    //when
    String result = fileService.getDir();
    //then
    assertThat(result).isEqualTo("/usr/local/atlas/tmp/");
  }


}