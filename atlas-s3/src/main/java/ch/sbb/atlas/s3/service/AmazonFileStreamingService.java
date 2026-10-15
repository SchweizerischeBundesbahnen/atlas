package ch.sbb.atlas.s3.service;

import ch.sbb.atlas.s3.config.AmazonBucket;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface AmazonFileStreamingService {

  StreamedFile streamFileAndDecompress(AmazonBucket amazonBucket, String fileToStream);

  StreamedFile streamFile(AmazonBucket amazonBucket, String fileToStream);

  StreamingResponseBody streamZipEntry(AmazonBucket amazonBucket, String fileToStream);

}
