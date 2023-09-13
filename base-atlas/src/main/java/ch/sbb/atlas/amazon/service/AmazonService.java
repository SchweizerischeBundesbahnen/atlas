package ch.sbb.atlas.amazon.service;

import com.amazonaws.services.s3.model.S3Object;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface AmazonService {

  URL putFile(AmazonBucket bucket, File file, String dir) throws IOException;

  URL putZipFile(AmazonBucket bucket, File file, String dir) throws IOException;

  URL putGzipFile(AmazonBucket bucket, File file, String dir) throws IOException;

  File pullFile(AmazonBucket bucket, String filePath) throws IOException;

  S3Object pullS3Object(AmazonBucket bucket, String filePath);

  StreamingResponseBody streamFile(AmazonBucket bucket, String fileToStream, boolean decompressGzip);

  void deleteFile(AmazonBucket bucket, String filePath);

  List<String> getS3ObjectKeysFromPrefix(AmazonBucket bucket, String dirPath, String prefix);
}
