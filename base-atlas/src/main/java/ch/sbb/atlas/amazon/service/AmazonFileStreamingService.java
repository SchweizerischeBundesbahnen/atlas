package ch.sbb.atlas.amazon.service;

import org.springframework.core.io.InputStreamResource;

public interface AmazonFileStreamingService {

  InputStreamResource streamFileAndDecompress(AmazonBucket amazonBucket, String fileToStream);

  InputStreamResource streamFile(AmazonBucket amazonBucket, String fileToStream);

}
