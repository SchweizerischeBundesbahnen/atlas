package ch.sbb.atlas.amazon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.config.AmazonConfigProps.AmazonBucketConfig;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;

class AmazonServiceTest {

  @Mock
  private AmazonS3 amazonS3;
  @Mock
  private FileService fileService;
  @Mock
  private AmazonBucketConfig amazonBucketConfig;
  @Mock
  private AmazonBucketClient amazonBucketClient;
  private AmazonServiceImpl amazonService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    List<AmazonBucketClient> amazonBucketClientList = new ArrayList<>();
    amazonBucketClientList.add(amazonBucketClient);
    amazonService = new AmazonServiceImpl(amazonBucketClientList, fileService);

    when(amazonBucketClient.getAmazonBucketConfig()).thenReturn(amazonBucketConfig);
    when(amazonBucketClient.getClient()).thenReturn(amazonS3);
    when(amazonBucketClient.getBucket()).thenReturn(AmazonBucket.EXPORT);
    when(amazonBucketConfig.getBucketName()).thenReturn("testBucket");

    PutObjectResult putObjectResult = new PutObjectResult();
    putObjectResult.setMetadata(new ObjectMetadata());
    putObjectResult.getMetadata().setContentLength(1);
    when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(putObjectResult);
  }

  @Test
  void shouldPutFile() throws IOException {
    //given
    Path tempFile = createTempFile();
    //when
    amazonService.putFile(AmazonBucket.EXPORT, tempFile.toFile(), "dir");
    //then
    verify(amazonS3).putObject(any(PutObjectRequest.class));
    verify(amazonS3).getUrl(Mockito.anyString(), Mockito.anyString());
  }

  @Test
  void shouldPutZipFile() throws IOException {
    //given
    Path tempFile = createTempFile();
    Path zipFile = createTempFile();
    //when
    when(fileService.zipFile(tempFile.toFile())).thenReturn(zipFile.toFile());

    amazonService.putZipFile(AmazonBucket.EXPORT, tempFile.toFile(), "dir");
    //then
    verify(fileService).zipFile(tempFile.toFile());
    verify(amazonS3).putObject(any(PutObjectRequest.class));
    verify(amazonS3).getUrl(Mockito.anyString(), Mockito.anyString());
  }

  @Test
  void shouldReturnBucketDir() throws IOException {
    //when
    String result = amazonService.getFilePathName(createTempFile().toFile(), "dev");
    //then
    assertThat(result).contains("dev/");
  }

  private Path createTempFile() throws IOException {
    Path tempFile = Files.createTempFile("tmp", ".csv");
    try (FileInputStream fileInputStream = new FileInputStream(tempFile.toFile())) {
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentType("application/zip");
      metadata.setContentLength(tempFile.toFile().length());
      new PutObjectRequest("bucket",
          tempFile.toFile().getName(),
          fileInputStream, metadata);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return tempFile;
  }

  @Test
  void shouldPullFile() throws IOException {
    //given
    when(fileService.getDir()).thenReturn("export");
    Path zipFile = createTempFile();

    S3Object s3Object = Mockito.mock(S3Object.class);
    when(s3Object.getObjectContent()).thenReturn(new S3ObjectInputStream(new FileInputStream(zipFile.toFile()),
        new HttpGet()));

    //when
    when(amazonS3.getObject("testBucket", "dir/desiredFile.zip")).thenReturn(s3Object);

    File pulledFile = amazonService.pullFile(AmazonBucket.EXPORT, "dir/desiredFile.zip");

    //then
    assertThat(pulledFile).isNotNull();
    verify(amazonS3).getObject("testBucket", "dir/desiredFile.zip");
    Files.delete(pulledFile.toPath());
  }

  @Test
  public void shouldGetClient(){
    //when
    AmazonS3 result = amazonService.getClient(AmazonBucket.EXPORT);
    //then
    assertThat(result).isNotNull();
  }

  @Test
  public void shouldGetAmazonBucketConfig(){
    //when
    AmazonBucketConfig result = amazonService.getAmazonBucketConfig(AmazonBucket.EXPORT);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getBucketName()).isEqualTo("testBucket");
  }

  @Test
  public void shouldPullFileAsStream() throws IOException {
    //given
    String filePath = "path/file";
    String bucketName= "testBucket";
    String testData = "Tesd data";
    byte[] dataBytes = testData.getBytes();

    S3Object s3Object = new S3Object();
    s3Object.setObjectContent(new ByteArrayInputStream(dataBytes));
    when(amazonS3.getObject(bucketName,filePath)).thenReturn(s3Object);

    //when
    InputStreamResource result = amazonService.pullFileAsStream(AmazonBucket.EXPORT, filePath);
    //then
    assertThat(result).isNotNull();
    String stringResult = IOUtils.toString(result.getInputStream(), StandardCharsets.UTF_8);
    assertThat(stringResult).isEqualTo(testData);
  }
  @Test
  public void shouldGetLatestJsonUploadedObject() {
    //given
    String filePath = "path/file";
    String filePrefix = "prefix";
    String bucketName= "testBucket";
    String testData = "Tesd data";
    byte[] dataBytes = testData.getBytes();

    S3Object s3Object = new S3Object();
    s3Object.setObjectContent(new ByteArrayInputStream(dataBytes));
    when(amazonS3.getObject(bucketName,filePath)).thenReturn(s3Object);
    ListObjectsV2Result listObjectsV2Result = mock(ListObjectsV2Result.class);
    listObjectsV2Result.setBucketName(bucketName);
    when(amazonS3.listObjectsV2(bucketName,filePrefix)).thenReturn(listObjectsV2Result);
    S3ObjectSummary s3ObjectSummary1 = new S3ObjectSummary();
    s3ObjectSummary1.setKey("path/file/file1.json");
    s3ObjectSummary1.setKey(bucketName);
    Date first = Date.from(LocalDate.of(2020,1,1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    s3ObjectSummary1.setLastModified(first);
    S3ObjectSummary s3ObjectSummary2 = new S3ObjectSummary();
    s3ObjectSummary2.setKey("path/file/file2.json");
    s3ObjectSummary2.setBucketName(bucketName);
    Date second = Date.from(LocalDate.of(2020,1,2).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    s3ObjectSummary2.setLastModified(second);
    when(listObjectsV2Result.getObjectSummaries()).thenReturn(List.of(s3ObjectSummary1,s3ObjectSummary2));
    //when
    String result = amazonService.getLatestJsonUploadedObject(AmazonBucket.EXPORT, filePrefix,filePath);
    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("path/file/file2.json");
  }

}