package ch.sbb.atlas.amazon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class AmazonServiceTest {

  @Mock
  private AmazonS3 amazonS3;

  @Mock
  private FileService fileService;

  private AmazonServiceImpl amazonService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    amazonService = new AmazonServiceImpl(amazonS3, fileService);
  }

  @Test
  public void shouldPutFile() throws IOException {
    //given
    Path tempFile = createTempFile();
    amazonService.setBucketDir("line");
    //when
    amazonService.putFile(tempFile.toFile());
    //then
    verify(amazonS3).putObject(Mockito.any(PutObjectRequest.class));
    verify(amazonS3).getUrl(Mockito.anyString(), Mockito.anyString());
  }

  @Test
  public void shouldPutZipFile() throws IOException {
    //given
    Path tempFile = createTempFile();
    amazonService.setBucketDir("line");
    when(fileService.zipFile(tempFile.toFile())).thenCallRealMethod();
    //when
    amazonService.putZipFile(tempFile.toFile());
    //then
    verify(fileService).zipFile(tempFile.toFile());
    verify(amazonS3).putObject(Mockito.any(PutObjectRequest.class));
    verify(amazonS3).getUrl(Mockito.anyString(), Mockito.anyString());
  }

  @Test
  public void shouldGetBucketNameFromActiveProfileProd() {
    //given
    amazonService.setActiveProfile("prod");
    //when
    String result = amazonService.getBucketNameFromActiveProfile();
    //then
    assertThat(result).isEqualTo("atlas-data-export-prod");
  }

  @Test
  public void shouldGetBucketNameFromActiveProfileDev() {
    //given
    amazonService.setActiveProfile("dev");
    //when
    String result = amazonService.getBucketNameFromActiveProfile();
    //then
    assertThat(result).isEqualTo("atlas-data-export-dev-dev");
  }

  @Test
  public void shouldGetBucketNameFromActiveProfileLocal() {
    //given
    amazonService.setActiveProfile("local");
    //when
    String result = amazonService.getBucketNameFromActiveProfile();
    //then
    assertThat(result).isEqualTo("atlas-data-export-dev-dev");
  }

  @Test
  public void shouldGetBucketNameFromActiveProfileTest() {
    //given
    amazonService.setActiveProfile("test");
    //when
    String result = amazonService.getBucketNameFromActiveProfile();
    //then
    assertThat(result).isEqualTo("atlas-data-export-test-dev");
  }

  @Test
  public void shouldGetBucketNameFromActiveProfileInt() {
    //given
    amazonService.setActiveProfile("int");
    //when
    String result = amazonService.getBucketNameFromActiveProfile();
    //then
    assertThat(result).isEqualTo("atlas-data-export-int-dev");
  }

  @Test
  public void shouldThrowExceptionWhenUnkonwProfile() {
    //given
    amazonService.setActiveProfile("ciao");
    //when
    assertThatExceptionOfType(IllegalStateException.class).isThrownBy(
        () -> amazonService.getBucketNameFromActiveProfile());
  }

  @Test
  public void shouldThrowExceptionWhenBucketDirNull() {
    //when
    assertThatExceptionOfType(IllegalStateException.class).isThrownBy(
        () -> amazonService.getFilePathName(createTempFile().toFile()));
  }

  @Test
  public void shouldRetrunBucketDir() throws IOException {
    //given
    amazonService.setBucketDir("dev");
    //when
    String result = amazonService.getFilePathName(createTempFile().toFile());
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


}