package ch.sbb.atlas.s3.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.s3.config.AmazonBucket;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

class AmazonFileStreamingServiceTest {

  @Mock
  private AmazonService amazonService;

  private AmazonFileStreamingService amazonFileStreamingService;

  @Mock
  private FileServiceImpl fileService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    amazonFileStreamingService = new AmazonFileStreamingServiceImpl(amazonService, fileService);
  }

  @Test
  void shouldStreamFileAndDecompress() throws IOException {
    //given
    String testData = "Tesd data";
    byte[] dataBytes = testData.getBytes();

    when(amazonService.pullS3Object(any(),any())).thenReturn(new ByteArrayInputStream(dataBytes));
    when(fileService.gzipDecompress(any(InputStream.class))).thenReturn(dataBytes);
    //when
    StreamedFile response = amazonFileStreamingService.streamFileAndDecompress(AmazonBucket.EXPORT,
        "file.json");

    //then
    assertThat(response.contentLength()).isEqualTo(ContentLength.of(dataBytes.length));
    String result = new String(response.resource().getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    assertThat(result).isEqualTo(testData);
  }

  @Test
  void shouldStreamFile() throws IOException {
    //given
    String testData = "Tesd data";
    InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(testData.getBytes()));
    when(amazonService.pullFileAsStream(any(), any())).thenReturn(inputStreamResource);
    when(amazonService.getObjectContentLength(AmazonBucket.EXPORT, "file.json")).thenReturn(ContentLength.of(9L));

    //when
    StreamedFile response = amazonFileStreamingService.streamFile(AmazonBucket.EXPORT, "file.json");

    //then
    assertThat(response.contentLength()).isEqualTo(ContentLength.of(9L));
    String result =  new String(response.resource().getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    assertThat(result).isEqualTo("Tesd data");
  }

  @Test
  void shouldStreamZipEntry() throws IOException {
    //given
    String testData = "Test data";
    byte[] zipBytes = zip("file.xml", testData.getBytes(StandardCharsets.UTF_8));
    when(amazonService.pullS3Object(any(), any())).thenReturn(new ByteArrayInputStream(zipBytes));

    //when
    StreamingResponseBody streamingResponseBody = amazonFileStreamingService.streamZipEntry(AmazonBucket.EXPORT,
        "file.xml.zip");
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    streamingResponseBody.writeTo(outputStream);

    //then
    assertThat(outputStream.toString(StandardCharsets.UTF_8)).isEqualTo(testData);
  }

  @Test
  void shouldThrowIllegalStateExceptionWhenZipHasNoEntry() throws IOException {
    //given
    byte[] emptyZipBytes;
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
      zipOutputStream.finish();
      emptyZipBytes = byteArrayOutputStream.toByteArray();
    }
    when(amazonService.pullS3Object(any(), any())).thenReturn(new ByteArrayInputStream(emptyZipBytes));

    //when
    StreamingResponseBody streamingResponseBody = amazonFileStreamingService.streamZipEntry(AmazonBucket.EXPORT,
        "empty.xml.zip");

    //then
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> streamingResponseBody.writeTo(outputStream));
  }

  @Test
  void shouldThrowIllegalStateExceptionWhenZipEntryExceedsMaxUncompressedSize() throws IOException {
    // Given
    byte[] zipBytes = zip("file.xml", "0123456789".getBytes(StandardCharsets.UTF_8));
    when(amazonService.pullS3Object(any(), any())).thenReturn(new ByteArrayInputStream(zipBytes));
    AmazonFileStreamingService limited = new AmazonFileStreamingServiceImpl(amazonService, fileService, 5);

    // When
    StreamingResponseBody streamingResponseBody = limited.streamZipEntry(AmazonBucket.EXPORT, "file.xml.zip");

    // Then
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> streamingResponseBody.writeTo(outputStream))
        .withMessageContaining("maximum uncompressed size");
  }

  private byte[] zip(String entryName, byte[] content) throws IOException {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
      zipOutputStream.putNextEntry(new ZipEntry(entryName));
      zipOutputStream.write(content);
      zipOutputStream.closeEntry();
      zipOutputStream.finish();
      return byteArrayOutputStream.toByteArray();
    }
  }
}