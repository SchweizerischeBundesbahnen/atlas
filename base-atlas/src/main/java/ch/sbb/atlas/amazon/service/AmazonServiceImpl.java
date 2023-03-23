package ch.sbb.atlas.amazon.service;

import ch.sbb.atlas.amazon.config.AmazonConfigProps.AmazonBucketConfig;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AmazonServiceImpl implements AmazonService {

  private final List<AmazonBucketClient> amazonBucketClients;
  private final FileService fileService;

  @Override
  public URL putFile(AmazonBucket bucket, File file, String dir) throws IOException {
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.length());
    return putFileToBucket(bucket, file, dir, metadata);
  }

  private AmazonS3 getClient(AmazonBucket bucket) {
    return amazonBucketClients.stream().filter(i -> i.getBucket() == bucket).findFirst().orElseThrow().getClient();
  }
  private AmazonBucketConfig getAmazonBucketConfig(AmazonBucket bucket) {
    return amazonBucketClients.stream().filter(i -> i.getBucket() == bucket).findFirst().orElseThrow().getAmazonBucketConfig();
  }

  @Override
  public URL putZipFile(AmazonBucket bucket, File file, String dir) throws IOException {
    File zipFile = fileService.zipFile(file);
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType("application/zip");
    metadata.setContentLength(zipFile.length());
    URL url = putFileToBucket(bucket, zipFile, dir, metadata);
    Files.deleteIfExists(file.toPath());
    Files.deleteIfExists(zipFile.toPath());
    return url;
  }

  @Override
  public File pullFile(AmazonBucket bucket, String filePath) {
    S3Object s3Object = getClient(bucket).getObject(getAmazonBucketConfig(bucket).getBucketName(), filePath);
    String dir = fileService.getDir();
    File fileDownload = new File(dir + filePath.replaceAll("/", "_"));
    try (FileOutputStream fileOutputStream = new FileOutputStream(fileDownload);
        S3ObjectInputStream s3InputStream = s3Object.getObjectContent()) {
      fileOutputStream.write(s3InputStream.readAllBytes());
      return fileDownload;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteFile(AmazonBucket bucket, String filePath) {
    getClient(bucket).deleteObject(getAmazonBucketConfig(bucket).getBucketName(), filePath);
  }

  @Override
  public List<String> getS3ObjectKeysFromPrefix(AmazonBucket bucket, String dirPath, String prefix) {
    List<S3ObjectSummary> result = getClient(bucket).listObjectsV2(getAmazonBucketConfig(bucket).getBucketName(), getFilePathName(dirPath, prefix)).getObjectSummaries();
    return result.stream().map(S3ObjectSummary::getKey).toList();
  }

  private URL putFileToBucket(AmazonBucket bucket, File file, String dir, ObjectMetadata metadata) throws IOException {
    URL url;
    PutObjectRequest putObjectRequest;
    try (FileInputStream inputStream = new FileInputStream(file)) {
      String filePathName = getFilePathName(file, dir);
      putObjectRequest = new PutObjectRequest(getAmazonBucketConfig(bucket).getBucketName(), filePathName, inputStream,
          metadata);
      getClient(bucket).putObject(putObjectRequest);
      url = getClient(bucket).getUrl(getAmazonBucketConfig(bucket).getBucketName(), filePathName);
      return url;
    }
  }

  String getFilePathName(File file, String dir) {
    return dir + "/" + file.getName();
  }

  String getFilePathName(String dirPath, String fileName) {
    return "%s/%s".formatted(dirPath, fileName);
  }

}
