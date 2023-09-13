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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
    @Mock
    private AmazonBucketConfig amazonBucketConfig;
    @Mock
    private AmazonBucketClient amazonBucketClient;
    private AmazonServiceImpl amazonService;

    @BeforeEach
    public void setUp() {
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
    public void shouldPutFile() throws IOException {
        //given
        Path tempFile = createTempFile();
        //when
        when(amazonBucketConfig.getBucketName()).thenReturn("testBucket");
        amazonService.putFile(AmazonBucket.EXPORT, tempFile.toFile(), "dir");
        //then
        verify(amazonS3).putObject(any(PutObjectRequest.class));
        verify(amazonS3).getUrl(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void shouldPutZipFile() throws IOException {
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
    public void shouldReturnBucketDir() throws IOException {
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

}