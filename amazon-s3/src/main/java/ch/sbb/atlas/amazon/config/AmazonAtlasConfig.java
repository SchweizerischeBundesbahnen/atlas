package ch.sbb.atlas.amazon.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@UtilityClass
public class AmazonAtlasConfig {

    public static AmazonS3 configureAmazonS3Client(String accessKey,String secretKey,String region ) {
        AWSCredentials awsCredentials =
            new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder
            .standard()
            .withRegion(region)
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .build();
    }

}
