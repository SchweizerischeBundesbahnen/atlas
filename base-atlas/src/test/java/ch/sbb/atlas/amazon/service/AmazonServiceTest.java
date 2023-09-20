package ch.sbb.atlas.amazon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.config.AmazonConfigProps.AmazonBucketConfig;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

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
}