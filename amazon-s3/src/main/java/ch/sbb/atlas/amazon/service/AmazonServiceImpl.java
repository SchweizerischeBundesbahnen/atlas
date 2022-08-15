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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AmazonServiceImpl implements AmazonService {

  public static final String ATLAS_DATA_EXPORT_PREFIX = "atlas-data-export-";
  private final AmazonS3 amazonS3;
  private final FileService fileService;

  @Setter
  @Value("${spring.profiles.active:local}")
  private String activeProfile;

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

  private URL putFileToBucket(File file, ObjectMetadata metadata) {
    URL url;
    PutObjectRequest putObjectRequest;
    try (FileInputStream inputStream = new FileInputStream(file)) {
      String bucket = getBucketNameFromActiveProfile();
      log.info("Amazon Bucket Name: " + getBucketNameFromActiveProfile());
      String filePathName = getFilePathName(file);
      log.info("File Name: " + getFilePathName(file));
      putObjectRequest = new PutObjectRequest(bucket, filePathName, inputStream,
          metadata);
      amazonS3.putObject(putObjectRequest);
      url = amazonS3.getUrl(bucket, filePathName);
      return url;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  String getFilePathName(File file) {
    if (bucketDir == null) {
      throw new IllegalStateException(
          "Please define the property '${amazon.bucket.dir}' in the appropriate properties file!");
    }
    return bucketDir + "/" + file.getName();
  }

  @Override
  public String getBucketNameFromActiveProfile() {
    String profilesActive = System.getenv("SPRING_PROFILES_ACTIVE");
    if ("local".equals(activeProfile) || activeProfile == null) {
      activeProfile = "dev";
    }
    if ("prod".equals(activeProfile)) {
      return ATLAS_DATA_EXPORT_PREFIX + activeProfile;
    }
    if ("dev".equals(activeProfile) || "test".equals(activeProfile) ||
        "int".equals(activeProfile)) {
      return ATLAS_DATA_EXPORT_PREFIX + activeProfile + "-dev";
    }
    throw new IllegalStateException("Please define a valid [dev,test,int,prod] spring profile!");
  }

}
