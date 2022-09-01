package ch.sbb.atlas.amazon.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AmazonServiceImpl implements AmazonService {

  private final AmazonS3 amazonS3;
  private final FileService fileService;

  @Setter
  @Value("${amazon.bucket.name}")
  private String bucketName;

  @Setter
  @Value("${amazon.bucket.dir}")
  private String bucketDir;

  @Override
  public URL putFile(File file) throws IOException {
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.length());
    URL url = putFileToBucket(file, metadata);
    Files.deleteIfExists(file.toPath());
    return url;
  }

  @Override
  public URL putZipFile(File file) throws IOException {
    File zipFile = fileService.zipFile(file);
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType("application/zip");
    metadata.setContentLength(zipFile.length());
    URL url = putFileToBucket(zipFile, metadata);
    Files.deleteIfExists(file.toPath());
    Files.deleteIfExists(zipFile.toPath());
    return url;
  }

  private URL putFileToBucket(File file, ObjectMetadata metadata) throws IOException {
    URL url;
    PutObjectRequest putObjectRequest;
    try (FileInputStream inputStream = new FileInputStream(file)) {
      String filePathName = getFilePathName(file);
      putObjectRequest = new PutObjectRequest(bucketName, filePathName, inputStream,
          metadata);
      amazonS3.putObject(putObjectRequest);
      url = amazonS3.getUrl(bucketName, filePathName);
      return url;
    }
  }

  String getFilePathName(File file) {
    if (bucketDir == null) {
      throw new IllegalStateException(
          "Please define the property '${amazon.bucket.dir}' in the appropriate properties file!");
    }
    return bucketDir + "/" + file.getName();
  }

}
