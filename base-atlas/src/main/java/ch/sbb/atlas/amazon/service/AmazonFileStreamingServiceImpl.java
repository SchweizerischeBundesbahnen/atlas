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
  public StreamedFile streamFileAndDecompress(AmazonBucket amazonBucket, String fileToStream) {
    try (InputStream s3Object = amazonService.pullS3Object(amazonBucket, fileToStream)) {
      byte[] decompressed = fileService.gzipDecompress(s3Object);
      InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(decompressed));
      return new StreamedFile(resource, ContentLength.of(decompressed.length));
    } catch (IOException e) {
      throw new IllegalStateException("Could not stream the file", e);
    }
  }

  @Override
  public StreamedFile streamFile(AmazonBucket amazonBucket, String fileToStream) {
    InputStreamResource resource = amazonService.pullFileAsStream(amazonBucket, fileToStream);
    ContentLength contentLength = amazonService.getObjectContentLength(amazonBucket, fileToStream);
    return new StreamedFile(resource, contentLength);
  }

}
