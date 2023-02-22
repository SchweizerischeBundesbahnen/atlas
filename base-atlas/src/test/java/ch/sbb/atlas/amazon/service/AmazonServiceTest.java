package ch.sbb.atlas.amazon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.service.AmazonServiceImpl;
import ch.sbb.atlas.amazon.service.FileService;
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
        amazonService.setBucketName("bucket");
        //when
        amazonService.putFile(tempFile.toFile(), "dir");
        //then
        verify(amazonS3).putObject(Mockito.any(PutObjectRequest.class));
        verify(amazonS3).getUrl(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void shouldPutZipFile() throws IOException {
        //given
        Path tempFile = createTempFile();
        Path zipFile = createTempFile();
        amazonService.setBucketName("bucket");
        when(fileService.zipFile(tempFile.toFile())).thenReturn(zipFile.toFile());
        //when
        amazonService.putZipFile(tempFile.toFile(), "dir");
        //then
        verify(fileService).zipFile(tempFile.toFile());
        verify(amazonS3).putObject(Mockito.any(PutObjectRequest.class));
        verify(amazonS3).getUrl(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void shouldRetrunBucketDir() throws IOException {
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