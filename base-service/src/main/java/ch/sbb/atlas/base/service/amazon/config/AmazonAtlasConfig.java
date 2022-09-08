package ch.sbb.atlas.base.service.amazon.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AmazonAtlasConfig {

    public static AmazonS3 configureAmazonS3Client(AmazonConfigProps props) {
        AWSCredentials awsCredentials =
            new BasicAWSCredentials(props.getAccessKey(), props.getSecretKey());
        AmazonS3 s3Client = AmazonS3ClientBuilder
            .standard()
            .withRegion(props.getRegion())
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .build();
        s3Client.setBucketLifecycleConfiguration(props.getBucketName(),
            new BucketLifecycleConfiguration()
                .withRules(
                    new BucketLifecycleConfiguration.Rule()
                        .withId(props.getBucketName() + "-expiration-id")
                        .withFilter(new LifecycleFilter())
                        .withStatus(BucketLifecycleConfiguration.ENABLED)
                        .withExpirationInDays(props.getObjectExpirationDays())
                )
        );
        return s3Client;
    }

}
