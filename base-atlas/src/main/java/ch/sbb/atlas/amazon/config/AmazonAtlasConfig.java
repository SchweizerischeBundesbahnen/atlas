package ch.sbb.atlas.amazon.config;

import ch.sbb.atlas.amazon.config.AmazonConfigProps.AmazonBucketConfig;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonBucketClient;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration.Rule;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AmazonAtlasConfig {

    public static List<AmazonBucketClient> configureAmazonS3Client(AmazonConfigProps props) {
        Map<String, AmazonBucketConfig> amazonBucketConfig = props.getBucketConfigs();

        return amazonBucketConfig.entrySet().stream().map(entry -> {
            AmazonBucketConfig bucketConfig = entry.getValue();
            AWSCredentials awsCredentials =
                new BasicAWSCredentials(bucketConfig.getAccessKey(), bucketConfig.getSecretKey());
            AmazonS3 s3Client = AmazonS3ClientBuilder
                .standard()
                .withRegion(props.getRegion())
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

            Rule lifeCycleRules = getExpirationRule(bucketConfig);

            BucketLifecycleConfiguration bucketLifecycleConfiguration = new BucketLifecycleConfiguration().withRules(lifeCycleRules);
            s3Client.setBucketLifecycleConfiguration(bucketConfig.getBucketName(), bucketLifecycleConfiguration);

            return new AmazonBucketClient(AmazonBucket.fromProperty(entry.getKey()), s3Client, bucketConfig);
        }).toList();
    }

    Rule getExpirationRule(AmazonBucketConfig amazonBucketConfig) {
        return new Rule()
            .withId(amazonBucketConfig.getBucketName() + "-expiration-id")
            .withFilter(new LifecycleFilter())
            .withStatus(BucketLifecycleConfiguration.ENABLED)
            .withExpirationInDays(amazonBucketConfig.getObjectExpirationDays());
    }

}
