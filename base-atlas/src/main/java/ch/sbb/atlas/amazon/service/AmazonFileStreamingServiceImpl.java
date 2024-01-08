package ch.sbb.atlas.amazon.service;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RequiredArgsConstructor
public class AmazonFileStreamingServiceImpl implements AmazonFileStreamingService {

  private final AmazonService amazonService;
  private final FileService fileService;

  @Override
  public StreamingResponseBody streamFileAndDecompress(AmazonBucket amazonBucket, String fileToStream) {
    try(S3Object s3Object = amazonService.pullS3Object(amazonBucket, fileToStream);
        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
        InputStream inputStream = new ByteArrayInputStream(fileService.gzipDecompress(s3ObjectInputStream))){
      return inputStream::transferTo;

    } catch (IOException e) {
      throw new IllegalStateException("Could not stream the file", e);
    }
  }

  @Override
  public StreamingResponseBody streamFile(AmazonBucket amazonBucket, String fileToStream) {
    return amazonService.pullFileAsStream(amazonBucket, fileToStream);
  }

}
