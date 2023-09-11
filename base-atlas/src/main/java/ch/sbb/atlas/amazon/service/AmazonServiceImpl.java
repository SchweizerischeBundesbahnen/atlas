package ch.sbb.atlas.amazon.service;

import ch.sbb.atlas.amazon.config.AmazonConfigProps.AmazonBucketConfig;
import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.model.exception.NotFoundException.FileNotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AmazonServiceImpl implements AmazonService {

  private final List<AmazonBucketClient> amazonBucketClients;
  private final FileService fileService;

  private static byte[] gzipFile(byte[] bytes) throws IOException {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); GZIPOutputStream out = new GZIPOutputStream(baos); ){
      out.write(bytes, 0, bytes.length);
      out.finish();

      return baos.toByteArray();
    }
  }

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
  public URL putGzipFile(AmazonBucket bucket, File file, String dir) throws IOException {
    return putGzipFileToBucket(bucket, file, dir);
  }

  @Override
  public File pullFile(AmazonBucket bucket, String filePath) {
    try {
      S3Object s3Object = getClient(bucket).getObject(getAmazonBucketConfig(bucket).getBucketName(), filePath);
      String dir = fileService.getDir();
      return getFile(filePath, s3Object, dir);
    } catch (AmazonS3Exception e) {
      log.error(e.getMessage());
      throw new FileNotFoundException(filePath);
    }
  }

  private static File getFile(String filePath, S3Object s3Object, String dir) {
    File fileDownload = new File(dir + filePath.replaceAll("/", "_"));
    try (FileOutputStream fileOutputStream = new FileOutputStream(fileDownload);
        S3ObjectInputStream s3InputStream = s3Object.getObjectContent()) {
      fileOutputStream.write(s3InputStream.readAllBytes());
      return fileDownload;
    } catch (IOException e) {
      throw new FileException("There was a problem with downloading file with name: " + fileDownload.getName(), e);
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
      String filePathName = getFilePathName(file, dir) + ".gz";
      byte[] zippedBytes = gzipFile(inputStream.readAllBytes());
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentType("application/gzip");
      metadata.setContentLength(zippedBytes.length);
      putObjectRequest = new PutObjectRequest(getAmazonBucketConfig(bucket).getBucketName(), filePathName,
          new ByteArrayInputStream(zippedBytes),
          metadata);
      return executePutObjectRequest(putObjectRequest, bucket, filePathName);
    }
  }

  private URL executePutObjectRequest(PutObjectRequest putObjectRequest, AmazonBucket bucket, String filePathName) {
    PutObjectResult putObjectResult = getClient(bucket).putObject(putObjectRequest);
    URL url = getClient(bucket).getUrl(getAmazonBucketConfig(bucket).getBucketName(), filePathName);
    log.info("Upload to S3 completed. file={}, size={}", filePathName, putObjectResult.getMetadata().getContentLength());
    return url;
  }

  String getFilePathName(File file, String dir) {
    return dir + "/" + file.getName();
  }

  String getFilePathName(String dirPath, String fileName) {
    return "%s/%s".formatted(dirPath, fileName);
  }

}
