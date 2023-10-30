package ch.sbb.atlas.amazon.service;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface AmazonFileStreamingService {

  StreamingResponseBody streamFileAndDecompress(AmazonBucket amazonBucket, String fileToStream);

  StreamingResponseBody streamFile(AmazonBucket amazonBucket, String fileToStream);

}
