package ch.sbb.atlas.s3.service;

import ch.sbb.atlas.s3.config.AmazonBucket;

public interface AmazonFileStreamingService {

  StreamedFile streamFileAndDecompress(AmazonBucket amazonBucket, String fileToStream);

  StreamedFile streamFile(AmazonBucket amazonBucket, String fileToStream);

}
