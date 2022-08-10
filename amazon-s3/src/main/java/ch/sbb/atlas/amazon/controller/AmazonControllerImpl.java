package ch.sbb.atlas.amazon.controller;

import ch.sbb.atlas.amazon.service.FileService;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AmazonControllerImpl implements AmazonController {

  private final AmazonS3 amazonS3;
  private final FileService fileService;
  @Value("${spring.profiles.active:local}")
  private String activeProfile;

  @Override
  public ResponseEntity<URL> putFile(File file) {
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.length());
    URL url = putFileToBucket(file, metadata);
    return new ResponseEntity<>(url, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<URL> putZipFile(File file) {

    File zipFile = fileService.zipFile(file);

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType("application/zip");
    metadata.setContentLength(zipFile.length());
    URL url = putFileToBucket(zipFile, metadata);
    return new ResponseEntity<>(url, HttpStatus.OK);
  }

  private URL putFileToBucket(File file, ObjectMetadata metadata) {
    URL url;
    PutObjectRequest putObjectRequest;
    try {
      String bucket = getBucketNameFromActiveProfile();
      putObjectRequest = new PutObjectRequest(
          bucket,
          file.getName(),
          new FileInputStream(file),
          metadata);
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
