package ch.sbb.atlas.amazon.service;

import ch.sbb.atlas.amazon.config.AmazonConfigProps.AmazonBucketConfig;
import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.model.exception.NotFoundException.FileNotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AmazonServiceImpl implements AmazonService {

  public static final String JSON_FILE_EXTENSION = "json";
  public static final String GZ_EXTENSION = ".gz";
  public static final String CONTENT_TYPE = "application/gzip";
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
    metadata.setContentType(CONTENT_TYPE);
    metadata.setContentLength(zipFile.length());
    URL url = putFileToBucket(bucket, zipFile, dir, metadata);
    Files.deleteIfExists(file.toPath());
    Files.deleteIfExists(zipFile.toPath());
    return url;
  }

  @Override
  public URL putGzipFile(AmazonBucket bucket, File file, String dir) throws IOException {
    return putGzipFileToBucket(bucket, file, dir);
  }

  @Override
  public File pullFile(AmazonBucket bucket, String filePath) {
    try {
      S3Object s3Object = pullS3Object(bucket, filePath);
      return getFile(filePath, s3Object, fileService.getDir());
    } catch (AmazonS3Exception e) {
      log.error("AmazonS3Exception occurred! filePath={}, bucket={}", filePath, bucket, e);
      throw new FileNotFoundException(filePath);
    }
  }

  private static File getFile(String filePath, S3Object s3Object, String dir) {
    try {
      File downloadedFile =
          Files.createTempFile(Path.of(dir), filePath.replace("/", "_"), null).toFile();
      downloadedFile.deleteOnExit();
      log.warn(downloadedFile.getName());
      try (FileOutputStream fileOutputStream = new FileOutputStream(downloadedFile);
          S3ObjectInputStream s3InputStream = s3Object.getObjectContent()) {
        fileOutputStream.write(s3InputStream.readAllBytes());
        return downloadedFile;
      } catch (IOException e) {
        throw new FileException("There was a problem with downloading filePath=" + filePath + " to dir=" + dir, e);
      }
    } catch (IOException e) {
      throw new FileException("There was a problem with downloading filePath=" + filePath + " to dir=" + dir, e);
    }
  }

  @Override
  public S3Object pullS3Object(AmazonBucket bucket, String filePath) {
    return getClient(bucket).getObject(getAmazonBucketConfig(bucket).getBucketName(), filePath);
  }

  @Override
  public void deleteFile(AmazonBucket bucket, String filePath) {
    getClient(bucket).deleteObject(getAmazonBucketConfig(bucket).getBucketName(), filePath);
  }

  @Override
  public List<String> getS3ObjectKeysFromPrefix(AmazonBucket bucket, String dirPath, String prefix) {
    List<S3ObjectSummary> result = getClient(bucket).listObjectsV2(getAmazonBucketConfig(bucket).getBucketName(),
        getFilePathName(dirPath, prefix)).getObjectSummaries();
    return result.stream().map(S3ObjectSummary::getKey).toList();
  }

  @Override
  public String getLatestJsonUploadedObject(AmazonBucket bucket, String pathPrefix, String fileTypePrefix) {
    List<S3ObjectSummary> objectSummaries = getClient(bucket).listObjectsV2(getAmazonBucketConfig(bucket).getBucketName(),
        pathPrefix).getObjectSummaries();
    List<String> fileNameList = objectSummaries.stream()
        .filter(s3ObjectSummary ->
            s3ObjectSummary.getKey().contains(fileTypePrefix) && s3ObjectSummary.getKey().contains(JSON_FILE_EXTENSION))
        .sorted(Comparator.comparing(S3ObjectSummary::getLastModified).reversed())
        .map(S3ObjectSummary::getKey)
        .toList();
    if (!fileNameList.isEmpty() && fileNameList.get(0) != null) {
      return fileNameList.get(0);
    }
    throw new FileNotFoundException(
        "File with path prefix [{" + pathPrefix + "}] does not found on bucket [{" + bucket.getProperty() + "}]");
  }

  private URL putFileToBucket(AmazonBucket bucket, File file, String dir, ObjectMetadata metadata) throws IOException {
    PutObjectRequest putObjectRequest;
    try (FileInputStream inputStream = new FileInputStream(file)) {
      String filePathName = getFilePathName(file, dir);
      putObjectRequest = new PutObjectRequest(getAmazonBucketConfig(bucket).getBucketName(), filePathName, inputStream,
          metadata);
      return executePutObjectRequest(putObjectRequest, bucket, filePathName);
    }
  }

  private URL putGzipFileToBucket(AmazonBucket bucket, File file, String dir) throws IOException {
    PutObjectRequest putObjectRequest;
    try (FileInputStream inputStream = new FileInputStream(file)) {
      String filePathName = getFilePathName(file, dir) + GZ_EXTENSION;
      byte[] zippedBytes = fileService.gzipCompress(inputStream.readAllBytes());
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentType(CONTENT_TYPE);
      metadata.setContentLength(zippedBytes.length);
      putObjectRequest = new PutObjectRequest(getAmazonBucketConfig(bucket).getBucketName(), filePathName,
          new ByteArrayInputStream(zippedBytes),
          metadata);
      return executePutObjectRequest(putObjectRequest, bucket, filePathName);
    }
  }

  private URL executePutObjectRequest(PutObjectRequest putObjectRequest, AmazonBucket bucket, String filePathName) {
    getClient(bucket).putObject(putObjectRequest);
    URL url = getClient(bucket).getUrl(getAmazonBucketConfig(bucket).getBucketName(), filePathName);

    // Used for splunk dashboard to display exported files.
    log.info("Upload to S3 completed. file={}, size={}", filePathName, putObjectRequest.getMetadata().getContentLength());
    return url;
  }

  String getFilePathName(File file, String dir) {
    return dir + "/" + file.getName();
  }

  String getFilePathName(String dirPath, String fileName) {
    return "%s/%s".formatted(dirPath, fileName);
  }

}
