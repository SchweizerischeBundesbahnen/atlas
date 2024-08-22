package ch.sbb.atlas.amazon.service;

import ch.sbb.atlas.amazon.config.AmazonConfigProps.AmazonBucketConfig;
import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.model.exception.FileNotFoundOnS3Exception;
import ch.sbb.atlas.model.exception.NotFoundException.FileNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

@Slf4j
@RequiredArgsConstructor
public class AmazonServiceImpl implements AmazonService {

  public static final String JSON_FILE_EXTENSION = "json";
  public static final String GZ_EXTENSION = ".gz";
  public static final String CONTENT_TYPE_GZIP = "application/gzip";
  private final List<AmazonBucketClient> amazonBucketClients;
  private final FileService fileService;

  @Override
  public URL putFile(AmazonBucket bucket, File file, String dir) {
    return putFileToBucket(bucket, file, dir);
  }

  S3Client getClient(AmazonBucket bucket) {
    return amazonBucketClients.stream().filter(i -> i.getBucket() == bucket).findFirst().orElseThrow().getClient();
  }

  AmazonBucketConfig getAmazonBucketConfig(AmazonBucket bucket) {
    return amazonBucketClients.stream().filter(i -> i.getBucket() == bucket).findFirst().orElseThrow().getAmazonBucketConfig();
  }

  @Override
  public URL putZipFile(AmazonBucket bucket, File file, String dir) throws IOException {
    File zipFile = fileService.zipFile(file);

    String filePathName = getFilePathName(zipFile, dir);
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(getAmazonBucketConfig(bucket).getBucketName())
        .key(filePathName)
        .contentType(CONTENT_TYPE_GZIP)
        .contentLength(zipFile.length())
        .build();
    URL url = executePutObjectRequest(putObjectRequest, bucket, filePathName, RequestBody.fromFile(zipFile));

    Files.deleteIfExists(file.toPath());
    Files.deleteIfExists(zipFile.toPath());
    return url;
  }

  @Override
  public File pullFile(AmazonBucket bucket, String filePath) {
    try (InputStream s3Object = pullS3Object(bucket, filePath)) {
      return getFile(filePath, s3Object, fileService.getDir());
    } catch (S3Exception | IOException e) {
      log.error("S3Exception occurred! filePath={}, bucket={}", filePath, bucket, e);
      throw new FileNotFoundException(filePath);
    }
  }

  @Override
  public InputStreamResource pullFileAsStream(AmazonBucket bucket, String filePath) {
    InputStream s3Object = pullS3Object(bucket, filePath);
    return new InputStreamResource(s3Object);
  }

  private static File getFile(String filePath, InputStream s3Content, String dir) {
    File downloadedFile = new File(dir + filePath.replace("/", "_"));
    try (FileOutputStream fileOutputStream = new FileOutputStream(downloadedFile)) {
      fileOutputStream.write(s3Content.readAllBytes());
      return downloadedFile;
    } catch (IOException e) {
      throw new FileException("There was a problem with downloading filePath=" + filePath + " to dir=" + dir, e);
    }
  }

  @Override
  public InputStream pullS3Object(AmazonBucket bucket, String filePath) {
    log.info("Pull file {} from Amazon S3 Bucket.", filePath);
    try {
      return getClient(bucket).getObject(GetObjectRequest.builder()
          .bucket(getAmazonBucketConfig(bucket).getBucketName())
          .key(filePath).build());
    } catch (S3Exception amazonS3Exception) {
      throw new FileNotFoundOnS3Exception(filePath);
    }
  }

  @Override
  public void deleteFile(AmazonBucket bucket, String filePath) {
    getClient(bucket).deleteObject(DeleteObjectRequest.builder()
        .bucket(getAmazonBucketConfig(bucket).getBucketName())
        .key(filePath).build());
  }

  @Override
  public List<String> getS3ObjectKeysFromPrefix(AmazonBucket bucket, String dirPath, String prefix) {
    List<S3Object> result = getClient(bucket).listObjectsV2(ListObjectsV2Request.builder()
        .bucket(getAmazonBucketConfig(bucket).getBucketName())
        .prefix(getFilePathName(dirPath, prefix)).build()).contents();
    return result.stream().map(S3Object::key).toList();
  }

  @Override
  public String getLatestJsonUploadedObject(AmazonBucket bucket, String pathPrefix, String fileTypePrefix) {
    List<S3Object> s3Objects = getClient(bucket).listObjectsV2(ListObjectsV2Request.builder()
        .bucket(getAmazonBucketConfig(bucket).getBucketName())
        .prefix(pathPrefix).build()).contents();
    List<String> fileNameList = s3Objects.stream()
        .filter(s3Object -> s3Object.key().contains(fileTypePrefix) && s3Object.key().contains(JSON_FILE_EXTENSION))
        .sorted(Comparator.comparing(S3Object::lastModified).reversed())
        .map(S3Object::key)
        .toList();
    if (!fileNameList.isEmpty() && fileNameList.getFirst() != null) {
      return fileNameList.getFirst();
    }
    throw new FileNotFoundException(
        "File with path prefix [{" + pathPrefix + "}] does not found on bucket [{" + bucket.getProperty() + "}]");
  }

  private URL putFileToBucket(AmazonBucket bucket, File file, String dir) {
    String filePathName = getFilePathName(file, dir);
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(getAmazonBucketConfig(bucket).getBucketName())
        .key(filePathName)
        .build();
    return executePutObjectRequest(putObjectRequest, bucket, filePathName, RequestBody.fromFile(file));
  }

  @Override
  public URL putGzipFile(AmazonBucket bucket, File file, String dir) throws IOException {
    String filePathName = getFilePathName(file, dir) + GZ_EXTENSION;
    try (FileInputStream inputStream = new FileInputStream(file)) {
      byte[] zippedBytes = fileService.gzipCompress(inputStream.readAllBytes());
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .contentType(CONTENT_TYPE_GZIP)
          .contentLength((long) zippedBytes.length)
          .bucket(getAmazonBucketConfig(bucket).getBucketName())
          .key(filePathName)
          .build();
      return executePutObjectRequest(putObjectRequest, bucket, filePathName,
          RequestBody.fromInputStream(new ByteArrayInputStream(zippedBytes), zippedBytes.length));
    }
  }

  private URL executePutObjectRequest(PutObjectRequest putObjectRequest, AmazonBucket bucket, String filePathName,
      RequestBody requestBody) {
    getClient(bucket).putObject(putObjectRequest, requestBody);
    URL url = getClient(bucket)
        .utilities()
        .getUrl(GetUrlRequest.builder()
            .bucket(getAmazonBucketConfig(bucket).getBucketName())
            .key(filePathName).build());

    // Used for splunk dashboard to display exported files.
    log.info("Upload to S3 completed. file={}, size={}", filePathName, putObjectRequest.contentLength());
    return url;
  }

  String getFilePathName(File file, String dir) {
    return dir + "/" + file.getName();
  }

  String getFilePathName(String dirPath, String fileName) {
    return "%s/%s".formatted(dirPath, fileName);
  }

}
