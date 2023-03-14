package ch.sbb.atlas.amazon.config;

import ch.sbb.atlas.amazon.config.AmazonConfigProps.AmazonBucketConfig;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration.Rule;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AmazonAtlasConfig {

    public static AmazonS3 configureAmazonS3Client(AmazonConfigProps props, String bucketId) {
        AmazonBucketConfig amazonBucketConfig = props.getBucketConfigs().get(bucketId);

        AWSCredentials awsCredentials =
            new BasicAWSCredentials(amazonBucketConfig.getAccessKey(), amazonBucketConfig.getSecretKey());
        AmazonS3 s3Client = AmazonS3ClientBuilder
            .standard()
            .withRegion(props.getRegion())
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .build();

        Rule lifeCycleRules = getExpirationRule(amazonBucketConfig);

            BucketLifecycleConfiguration bucketLifecycleConfiguration = new BucketLifecycleConfiguration().withRules(lifeCycleRules);
            s3Client.setBucketLifecycleConfiguration(amazonBucketConfig.getBucketName(), bucketLifecycleConfiguration);
        return s3Client;
    }

    Rule getExpirationRule(AmazonBucketConfig amazonBucketConfig) {
        return new Rule()
            .withId(amazonBucketConfig.getBucketName() + "-expiration-id")
            .withFilter(new LifecycleFilter())
            .withStatus(BucketLifecycleConfiguration.ENABLED)
            .withExpirationInDays(amazonBucketConfig.getObjectExpirationDays());
    }

}
