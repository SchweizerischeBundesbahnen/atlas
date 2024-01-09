package ch.sbb.atlas.amazon.service;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
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
    try(S3Object s3Object = amazonService.pullS3Object(amazonBucket, fileToStream);
        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
        InputStream inputStream = new ByteArrayInputStream(fileService.gzipDecompress(s3ObjectInputStream))){
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
