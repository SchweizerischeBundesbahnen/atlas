package ch.sbb.atlas.amazon.service;

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
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AmazonServiceImpl implements AmazonService {

  private final AmazonS3 amazonS3;
  private final FileService fileService;

  @Setter
  @Value("${amazon.bucketName}")
  private String bucketName;

  @Override
  public URL putFile(File file, String dir) throws IOException {
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.length());
    return putFileToBucket(file, dir, metadata);
  }

  @Override
  public URL putZipFile(File file, String dir) throws IOException {
    File zipFile = fileService.zipFile(file);
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType("application/zip");
    metadata.setContentLength(zipFile.length());
    URL url = putFileToBucket(zipFile, dir, metadata);
    Files.deleteIfExists(file.toPath());
    Files.deleteIfExists(zipFile.toPath());
    return url;
  }

  @Override
  public File pullFile(String filePath) throws IOException {
    S3Object s3Object = amazonS3.getObject(bucketName, filePath);
    String dir = fileService.getDir();
    File fileDownload = new File(dir + filePath.replaceAll("/", "_"));
    try (FileOutputStream fileOutputStream = new FileOutputStream(fileDownload);
        S3ObjectInputStream s3InputStream = s3Object.getObjectContent()) {
      fileOutputStream.write(s3InputStream.readAllBytes());
      return fileDownload;
    }
  }

  @Override
  public List<String> getS3ObjectKeysFromPrefix(String dirPath, String prefix) {
    List<S3ObjectSummary> result = amazonS3.listObjectsV2(bucketName, getFilePathName(dirPath, prefix)).getObjectSummaries();
    return result.stream().map(S3ObjectSummary::getKey).toList();
  }

  private URL putFileToBucket(File file, String dir, ObjectMetadata metadata) throws IOException {
    URL url;
    PutObjectRequest putObjectRequest;
    try (FileInputStream inputStream = new FileInputStream(file)) {
      String filePathName = getFilePathName(file, dir);
      putObjectRequest = new PutObjectRequest(bucketName, filePathName, inputStream,
          metadata);
      amazonS3.putObject(putObjectRequest);
      url = amazonS3.getUrl(bucketName, filePathName);
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
