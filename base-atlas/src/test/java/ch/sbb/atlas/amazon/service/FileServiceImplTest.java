package ch.sbb.atlas.amazon.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

class FileServiceImplTest {

  private static final String SEPARATOR = File.separator;

  private final FileServiceImpl fileService = new FileServiceImpl();

  @Test
  void shouldCreateZipFile() throws IOException {
    //given
    final File dir = new File("./export");
    if (!dir.exists()) {
      assertThat(dir.mkdirs()).isTrue();
    }

    final Path tmpPath = Paths.get("./export/tmp.csv");
    File tmp = tmpPath.toFile();
    if (tmp.exists()) {
      assertThat(tmp.delete()).isTrue();
    }
    final Path tempFile = Files.createFile(tmpPath);

    //when
    tmp = Paths.get("./export/tmp.csv.zip").toFile();
    if (tmp.exists()) {
      assertThat(tmp.delete()).isTrue();
    }
    final File zipFile = fileService.zipFile(tempFile.toFile());

    //then
    assertThat(zipFile).isNotNull().hasName("tmp.csv.zip");

    // teardown
    assertThat(zipFile.delete()).isTrue();
    assertThat(tempFile.toFile().delete()).isTrue();
    assertThat(dir.delete()).isTrue();
  }

  @Test
  void shouldGetDirWhenActivatedProfileIsDefined() {
    //given
    fileService.setActiveProfile("dev");
    //when
    final String result = fileService.getDir();
    //then
    assertThat(result).isEqualTo("/usr/local/atlas/tmp/");
  }

  @Test
  void shouldGetDirWhenActivatedProfileIsNull() {
    //given
    fileService.setActiveProfile(null);
    //when
    final String result = fileService.getDir();
    //then
    assertThat(result).isEqualTo("." + SEPARATOR + "export" + SEPARATOR);
  }

  @Test
  void shouldGetDirWhenActivatedProfileIsLocal() {
    //given
    fileService.setActiveProfile("local");
    //when
    final String result = fileService.getDir();
    //then
    assertThat(result).isEqualTo("." + SEPARATOR + "export" + SEPARATOR);
  }

  @Test
  void shouldStreamFileToResponse() throws IOException {
    //given
    final File testFile = Files.createTempFile("testfile", ".txt").toFile();
    Files.writeString(testFile.toPath(), "Test Data");

    //when
    final StreamingResponseBody response = fileService.toStreamingResponse(testFile, new FileInputStream(testFile));

    //then
    assertThat(response).isNotNull();

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    response.writeTo(outputStream);
    assertThat(outputStream.toString()).isEqualTo("Test Data");

    // teardown
    if (testFile.exists()) {
      assertThat(testFile.delete()).isTrue();
    }
  }

  @Test
  void shouldCompressAndDecompressFile() throws IOException {
    //given
    final File testFile = Files.createTempFile("testfile", ".txt").toFile();
    Files.writeString(testFile.toPath(), "Test Data");

    //when
    byte[] compressedBytes;
    try (final FileInputStream fileInputStream = new FileInputStream(testFile)) {
      compressedBytes = fileService.gzipCompress(fileInputStream.readAllBytes());
    }

    final File compressed = Files.createTempFile("compressed", ".txt").toFile();
    Files.write(compressed.toPath(), compressedBytes);
    final byte[] decompressedBytes = fileService.gzipDecompress(compressed);

    //then
    assertThat(new String(decompressedBytes)).isEqualTo("Test Data");

    // teardown
    if (testFile.exists()){
      assertThat(testFile.delete()).isTrue();
    }
    if (compressed.exists()){
      assertThat(compressed.delete()).isTrue();
    }
  }

  @Test
  void shouldCompressAndDecompressS3ObjectInputStream() throws IOException {
    //given
    try (final InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json.gz")) {

      //when
      final byte[] bytes = fileService.gzipDecompress(inputStream);

      //then
      assertThat(new String(bytes, StandardCharsets.UTF_8)).isNotNull();
    }

  }

}
