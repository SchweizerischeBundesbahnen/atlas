package ch.sbb.atlas.amazon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

    S3Object s3Object = new S3Object();
    s3Object.setObjectContent(new ByteArrayInputStream(dataBytes));
    when(amazonService.pullS3Object(any(),any())).thenReturn(s3Object);
    when(fileService.gzipDecompress(any(S3ObjectInputStream.class))).thenReturn(dataBytes);
    //when
    StreamingResponseBody response = amazonFileStreamingService.streamFileAndDecompress(AmazonBucket.EXPORT,
        "file.json");

    //then
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    response.writeTo(outputStream);
    String output = outputStream.toString();
    assertThat(output).isEqualTo(testData);
  }

  @Test
  void shouldStreamFile() throws IOException {
    //given
    String testData = "Tesd data";
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(testData.getBytes());
    StreamingResponseBody responseBody = byteArrayInputStream::transferTo;
    when(amazonService.pullFileAsStream(any(), any())).thenReturn(responseBody);

    //when
    StreamingResponseBody response = amazonFileStreamingService.streamFile(AmazonBucket.EXPORT, "file.json");

    //then
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    response.writeTo(outputStream);
    String output = outputStream.toString();
    assertThat(output).isEqualTo("Tesd data");
  }
}