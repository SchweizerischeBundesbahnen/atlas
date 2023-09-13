package ch.sbb.atlas.amazon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.config.AmazonConfigProps.AmazonBucketConfig;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public class AmazonServiceStreamingTest {

    @Mock
    private AmazonS3 amazonS3;
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

        amazonService = new AmazonServiceImpl(amazonBucketClientList, new FileServiceImpl());

        when(amazonBucketClient.getAmazonBucketConfig()).thenReturn(amazonBucketConfig);
        when(amazonBucketClient.getClient()).thenReturn(amazonS3);
        when(amazonBucketClient.getBucket()).thenReturn(AmazonBucket.EXPORT);
        when(amazonBucketConfig.getBucketName()).thenReturn("testBucket");
    }

    @Test
    void shouldStreamServicePointVersionJsonFile() throws IOException {
        String fileName = "full_business_organisation_versions_2023-08-16.json.gz";
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());

        S3Object pullResult = mock(S3Object.class);
        when(pullResult.getObjectContent()).thenReturn(new S3ObjectInputStream(new FileInputStream(file), new HttpGet()));
        when(amazonS3.getObject(anyString(), anyString())).thenReturn(pullResult);

        StreamingResponseBody result = amazonService.streamFile(AmazonBucket.EXPORT, fileName, true);
        assertThat(result).isNotNull();
    }

    @Test
    void shouldStreamGzipFile() throws IOException {
        String fileName = "full_business_organisation_versions_2023-08-16.json.gz";
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());

        S3Object pullResult = mock(S3Object.class);
        when(pullResult.getObjectContent()).thenReturn(new S3ObjectInputStream(new FileInputStream(file), new HttpGet()));
        when(amazonS3.getObject(anyString(), anyString())).thenReturn(pullResult);

        StreamingResponseBody result = amazonService.streamFile(AmazonBucket.EXPORT, fileName, false);
        assertThat(result).isNotNull();
    }

}