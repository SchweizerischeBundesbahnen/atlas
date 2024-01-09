package ch.sbb.atlas.amazon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
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

    S3Object s3Object = new S3Object();
    s3Object.setObjectContent(new ByteArrayInputStream(dataBytes));
    when(amazonService.pullS3Object(any(),any())).thenReturn(s3Object);
    when(fileService.gzipDecompress(any(S3ObjectInputStream.class))).thenReturn(dataBytes);
    //when
    InputStreamResource response = amazonFileStreamingService.streamFileAndDecompress(AmazonBucket.EXPORT,
        "file.json");

    //then
    String result = IOUtils.toString(response.getInputStream(), StandardCharsets.UTF_8);
    assertThat(result).isEqualTo(testData);
  }

  @Test
  void shouldStreamFile() throws IOException {
    //given
    String testData = "Tesd data";
    InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(testData.getBytes()));
    when(amazonService.pullFileAsStream(any(), any())).thenReturn(inputStreamResource);

    //when
    InputStreamResource response = amazonFileStreamingService.streamFile(AmazonBucket.EXPORT, "file.json");

    //then
    String result = IOUtils.toString(response.getInputStream(), StandardCharsets.UTF_8);
    assertThat(result).isEqualTo("Tesd data");
  }
}