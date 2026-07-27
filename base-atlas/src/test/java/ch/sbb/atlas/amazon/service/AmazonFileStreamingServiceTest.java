package ch.sbb.atlas.amazon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;

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
}