package ch.sbb.atlas.amazon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

class AmazonFileStreamingServiceTest {

  @Mock
  private AmazonService amazonService;

  private AmazonFileStreamingService amazonFileStreamingService;
  private FileServiceImpl fileService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    fileService = new FileServiceImpl();
    amazonFileStreamingService = new AmazonFileStreamingServiceImpl(amazonService, fileService);
  }

  @Test
  void shouldStreamFileAndDecompress() throws IOException {
    File file = new File("testfile");
    Files.writeString(file.toPath(), "Tesd data");
    file.deleteOnExit();

    byte[] compressedBytes = fileService.gzipCompress(Files.readAllBytes(file.toPath()));
    File compressed = new File("compressed");
    Files.write(compressed.toPath(), compressedBytes);
    compressed.deleteOnExit();

    when(amazonService.pullFile(any(), any())).thenReturn(compressed);

    StreamingResponseBody response = amazonFileStreamingService.streamFileAndDecompress(AmazonBucket.EXPORT,
        "file.json");

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    response.writeTo(outputStream);
    String output = outputStream.toString();
    assertThat(output).isEqualTo("Tesd data");
  }

  @Test
  void shouldStreamFile() throws IOException {
    File file = new File("testfile");
    Files.writeString(file.toPath(), "Tesd data");
    file.deleteOnExit();

    when(amazonService.pullFile(any(), any())).thenReturn(file);

    StreamingResponseBody response = amazonFileStreamingService.streamFile(AmazonBucket.EXPORT, "file.json");

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    response.writeTo(outputStream);
    String output = outputStream.toString();
    assertThat(output).isEqualTo("Tesd data");
  }
}