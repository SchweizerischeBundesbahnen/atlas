package ch.sbb.atlas.amazon.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.amazonaws.services.s3.model.S3Object;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

class FileServiceImplTest {

  private static final String SEPARATOR = File.separator;

  private final FileServiceImpl fileService = new FileServiceImpl();

  @Test
  void shouldCreateZipFile() throws IOException {
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
  void shouldGetDirWhenActivatedProfileIsDefined() {
    //given
    fileService.setActiveProfile("dev");
    //when
    String result = fileService.getDir();
    //then
    assertThat(result).isEqualTo("/usr/local/atlas/tmp/");
  }

  @Test
  void shouldGetDirWhenActivatedProfileIsNull() {
    //given
    fileService.setActiveProfile(null);
    //when
    String result = fileService.getDir();
    //then
    assertThat(result).isEqualTo("." + SEPARATOR + "export" + SEPARATOR);
  }

  @Test
  void shouldGetDirWhenActivatedProfileIsLocal() {
    //given
    fileService.setActiveProfile("local");
    //when
    String result = fileService.getDir();
    //then
    assertThat(result).isEqualTo("." + SEPARATOR + "export" + SEPARATOR);
  }

  @Test
  void shouldStreamFileToResponse() throws IOException {
    //given
    File file = new File("testfile");
    Files.writeString(file.toPath(), "Test Data");
    file.deleteOnExit();

    //when
    StreamingResponseBody response = fileService.toStreamingResponse(file, new FileInputStream(file));

    //then
    assertThat(response).isNotNull();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    response.writeTo(outputStream);
    String output = outputStream.toString();
    assertThat(output).isEqualTo("Test Data");
  }

  @Test
  void shouldCompressAndDecompressFile() throws IOException {
    //given
    File file = new File("testfile");
    Files.writeString(file.toPath(), "Test Data");
    file.deleteOnExit();

    //when
    byte[] compressedBytes;
    try (FileInputStream fileInputStream = new FileInputStream(file)) {
      compressedBytes = fileService.gzipCompress(fileInputStream.readAllBytes());
    }

    File compressed = new File("compressed");
    compressed.deleteOnExit();
    Files.write(compressed.toPath(), compressedBytes);
    byte[] decompressedBytes = fileService.gzipDecompress(compressed);

    //then

    assertThat(new String(decompressedBytes)).isEqualTo("Test Data");
  }

  @Test
  void shouldCompressAndDecompressS3ObjectInputStream() throws IOException {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json.gz");
        S3Object s3Object = new S3Object()) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      s3Object.setObjectContent(inputStreamResource.getInputStream());

      //when
      byte[] bytes = fileService.gzipDecompress(s3Object.getObjectContent());

      //then
      String result = new String(bytes, StandardCharsets.UTF_8);
      assertThat(result).isNotNull();

    }

  }

}
