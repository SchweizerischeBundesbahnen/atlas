package ch.sbb.atlas.amazon.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import org.springframework.core.io.InputStreamResource;

public interface AmazonService {

  URL putFile(AmazonBucket bucket, File file, String dir);

  URL putZipFileCleanupBoth(AmazonBucket bucket, File file, String dir) throws IOException;

  URL putGzipFile(AmazonBucket bucket, File file, String dir) throws IOException;

  URL putZipFileCleanupZip(AmazonBucket bucket, File file, String dir) throws IOException;

  File pullFile(AmazonBucket bucket, String filePath);

  InputStreamResource pullFileAsStream(AmazonBucket bucket, String filePath);

  InputStream pullS3Object(AmazonBucket bucket, String filePath);

  void deleteFile(AmazonBucket bucket, String filePath);

  List<String> getS3ObjectKeysFromPrefix(AmazonBucket bucket, String dirPath, String prefix);

  String getLatestJsonUploadedObject(AmazonBucket bucket, String pathPrefix, String fileTypePrefix);

}
