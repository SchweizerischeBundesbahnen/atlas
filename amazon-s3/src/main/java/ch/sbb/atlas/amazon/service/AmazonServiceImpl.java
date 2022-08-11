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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AmazonServiceImpl implements AmazonService {

  private final AmazonS3 amazonS3;
  private final FileService fileService;
  @Value("${spring.profiles.active:local}")
  private String activeProfile;

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

  private URL putFileToBucket(File file, ObjectMetadata metadata) {
    URL url;
    PutObjectRequest putObjectRequest;
    try (FileInputStream inputStream = new FileInputStream(file)) {
      String bucket = getBucketNameFromActiveProfile();
      putObjectRequest = new PutObjectRequest(bucket, file.getName(), inputStream, metadata);
      amazonS3.putObject(putObjectRequest);
      url = amazonS3.getUrl(bucket, file.getName());
      return url;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String getBucketNameFromActiveProfile() {
    if ("local".equals(activeProfile)) {
      activeProfile = "dev";
    }
    return "atlas-data-broker-" + activeProfile;
  }

}
