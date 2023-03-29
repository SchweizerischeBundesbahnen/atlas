package ch.sbb.atlas.amazon.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface AmazonService {

  URL putFile(AmazonBucket bucket, File file, String dir) throws IOException;

  URL putZipFile(AmazonBucket bucket, File file, String dir) throws IOException;

  File pullFile(AmazonBucket bucket, String filePath) throws IOException;

  void deleteFile(AmazonBucket bucket, String filePath);

  List<String> getS3ObjectKeysFromPrefix(AmazonBucket bucket, String dirPath, String prefix);
}
