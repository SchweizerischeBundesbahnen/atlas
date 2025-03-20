package ch.sbb.atlas.amazon.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;

@RequiredArgsConstructor
public class AmazonFileStreamingServiceImpl implements AmazonFileStreamingService {

  private final AmazonService amazonService;
  private final FileService fileService;

  @Override
  public InputStreamResource streamFileAndDecompress(AmazonBucket amazonBucket, String fileToStream) {
    try (InputStream s3Object = amazonService.pullS3Object(amazonBucket, fileToStream);
        InputStream inputStream = new ByteArrayInputStream(fileService.gzipDecompress(s3Object))) {
      return new InputStreamResource(inputStream);
    } catch (IOException e) {
      throw new IllegalStateException("Could not stream the file", e);
    }
  }

  @Override
  public InputStreamResource streamFile(AmazonBucket amazonBucket, String fileToStream) {
    return amazonService.pullFileAsStream(amazonBucket, fileToStream);
  }

}
