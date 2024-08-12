package ch.sbb.atlas.amazon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.config.AmazonConfigProps.AmazonBucketConfig;
import ch.sbb.atlas.model.exception.FileNotFoundOnS3Exception;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

class AmazonServiceTest {

  @Mock
  private S3Client s3Client;
  @Mock
  private S3Utilities s3Utilities;
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
    when(amazonBucketClient.getClient()).thenReturn(s3Client);
    when(amazonBucketClient.getBucket()).thenReturn(AmazonBucket.EXPORT);
    when(amazonBucketConfig.getBucketName()).thenReturn("testBucket");

    PutObjectResponse putObjectResult = PutObjectResponse.builder().build();
    when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(putObjectResult);

    when(s3Client.utilities()).thenReturn(s3Utilities);
  }

  @Test
  void shouldPutFile() throws IOException {
    //given
    Path tempFile = createTempFile();
    //when
    amazonService.putFile(AmazonBucket.EXPORT, tempFile.toFile(), "dir");
    //then
    verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    verify(s3Utilities).getUrl(any(GetUrlRequest.class));
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
    verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    verify(s3Utilities).getUrl(any(GetUrlRequest.class));
  }

  @Test
  void shouldReturnBucketDir() throws IOException {
    //when
    String result = amazonService.getFilePathName(createTempFile().toFile(), "dev");
    //then
    assertThat(result).contains("dev/");
  }

  private Path createTempFile() throws IOException {
    return Files.createTempFile("tmp", ".csv");
  }

  @Test
  void shouldPullFile() throws IOException {
    //given
    when(fileService.getDir()).thenReturn("export");
    Path zipFile = createTempFile();

    ResponseInputStream<GetObjectResponse> responseInputStream = new ResponseInputStream<>(GetObjectResponse.builder().build(),
        new FileInputStream(zipFile.toFile()));

    //when
    when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseInputStream);

    File pulledFile = amazonService.pullFile(AmazonBucket.EXPORT, "dir/desiredFile.zip");

    //then
    assertThat(pulledFile).isNotNull();
    verify(s3Client).getObject(any(GetObjectRequest.class));
    Files.delete(pulledFile.toPath());
  }

  @Test
  void shouldThrowFileNotFoundOnS3ExceptionOnPullFile() {
    when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(
        S3Exception.builder().message("The specified key does not exist.").build());

    assertThatExceptionOfType(FileNotFoundOnS3Exception.class)
        .isThrownBy(() -> amazonService.pullFile(AmazonBucket.EXPORT, "dir" + "/desiredFile.zip"))
        .extracting(i -> i.getErrorResponse().getError())
        .isEqualTo("File dir/desiredFile.zip not found on atlas amazon s3 bucket.");
  }

  @Test
  void shouldGetClient() {
    //when
    S3Client result = amazonService.getClient(AmazonBucket.EXPORT);
    //then
    assertThat(result).isNotNull();
  }

  @Test
  void shouldGetAmazonBucketConfig() {
    //when
    AmazonBucketConfig result = amazonService.getAmazonBucketConfig(AmazonBucket.EXPORT);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getBucketName()).isEqualTo("testBucket");
  }

  @Test
  void shouldPullFileAsStream() throws IOException {
    //given
    String filePath = "path/file";
    String testData = "Tesd data";
    byte[] dataBytes = testData.getBytes();

    ResponseInputStream<GetObjectResponse> responseInputStream = new ResponseInputStream<>(GetObjectResponse.builder().build(),
        new ByteArrayInputStream(dataBytes));

    //when
    when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseInputStream);

    //when
    InputStreamResource result = amazonService.pullFileAsStream(AmazonBucket.EXPORT, filePath);
    //then
    assertThat(result).isNotNull();
    String stringResult = IOUtils.toString(result.getInputStream(), StandardCharsets.UTF_8);
    assertThat(stringResult).isEqualTo(testData);
  }

  @Test
  void shouldGetLatestJsonUploadedObject() {
    //given
    String filePath = "path/file";
    String filePrefix = "prefix";
    String testData = "Tesd data";
    byte[] dataBytes = testData.getBytes();

    ResponseInputStream<GetObjectResponse> responseInputStream = new ResponseInputStream<>(GetObjectResponse.builder().build(),
        new ByteArrayInputStream(dataBytes));
    when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseInputStream);

    Instant first = LocalDate.of(2020, 1, 1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
    Instant second = LocalDate.of(2020, 1, 2).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
    ListObjectsV2Response listObjectsV2Result = mock(ListObjectsV2Response.class);
    when(listObjectsV2Result.contents()).thenReturn(List.of(S3Object.builder()
            .key("path/file/file1.json")
            .lastModified(first)
            .build(),
        S3Object.builder()
            .key("path/file/file2.json")
            .lastModified(second)
            .build()));
    when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Result);

    //when
    String result = amazonService.getLatestJsonUploadedObject(AmazonBucket.EXPORT, filePrefix, filePath);
    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("path/file/file2.json");
  }

  @Test
  void shouldDeleteFile() {
    //when
    amazonService.deleteFile(AmazonBucket.EXPORT, "service_points/full/full-swiss-only-service_point-2024-07-13.csv.zip");
    //then
    verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
  }

  @Test
  void shouldGetObjectKeysByPrefix() {
    when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(
        ListObjectsV2Response.builder().contents(S3Object.builder()
            .key("full-swiss-only-service_point-2024-07-13.csv.zip")
            .build()).build());
    //when
    amazonService.getS3ObjectKeysFromPrefix(AmazonBucket.EXPORT, "service_points/full", "full-swiss-only-service_point-");
    //then
    verify(s3Client).listObjectsV2(any(ListObjectsV2Request.class));
  }

  @Test
  void shouldPutGzipFileToBucket() throws IOException {
    // Fake compress
    when(fileService.gzipCompress(any())).thenAnswer(i -> i.getArgument(0));
    when(s3Utilities.getUrl(any(GetUrlRequest.class))).thenAnswer(i -> URI.create(
            "https://atlas-data-export-dev-dev.s3.eu-central-1.amazonaws.com/" + i.getArgument(0, GetUrlRequest.class).key())
        .toURL());

    //when
    File file = createTempFile().toFile();
    URL url = amazonService.putGzipFile(AmazonBucket.EXPORT, file, "service_points/full");

    verify(fileService).gzipCompress(any());
    verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    verify(s3Utilities).getUrl(any(GetUrlRequest.class));

    assertThat(url.toString()).isNotNull().isEqualTo("https://atlas-data-export-dev-dev.s3.eu-central-1.amazonaws"
        + ".com/service_points/full/" + file.getName() + ".gz");
  }
}