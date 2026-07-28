package ch.sbb.atlas.amazon.service;

public interface AmazonFileStreamingService {

  StreamedFile streamFileAndDecompress(AmazonBucket amazonBucket, String fileToStream);

  StreamedFile streamFile(AmazonBucket amazonBucket, String fileToStream);

}
