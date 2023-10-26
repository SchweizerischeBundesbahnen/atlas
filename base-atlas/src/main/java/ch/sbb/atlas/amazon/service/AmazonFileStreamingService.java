package ch.sbb.atlas.amazon.service;

import java.io.File;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface AmazonFileStreamingService {

  StreamingResponseBody streamFileAndDecompress(AmazonBucket amazonBucket, String fileToStream);

  StreamingResponseBody streamFile(AmazonBucket amazonBucket, String fileToStream);

  File downloadFile(AmazonBucket amazonBucket, String fileToStream);
}
