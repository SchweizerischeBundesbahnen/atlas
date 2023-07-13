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
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

            setBucketLifecycleConfiguration(bucketConfig, s3Client);

            return new AmazonBucketClient(AmazonBucket.fromProperty(entry.getKey()), s3Client, bucketConfig);
        }).toList();
    }

    static void setBucketLifecycleConfiguration(AmazonBucketConfig bucketConfig, AmazonS3 s3Client) {
        Rule lifeCycleRules = getExpirationRule(bucketConfig);

        BucketLifecycleConfiguration bucketLifecycleConfiguration = new BucketLifecycleConfiguration().withRules(lifeCycleRules);
        BucketLifecycleConfiguration currentConfig = s3Client.getBucketLifecycleConfiguration(bucketConfig.getBucketName());
        if (!currentConfig.getRules().stream().allMatch(rule -> ruleEquals(rule, lifeCycleRules))) {
            log.info("Current BucketLifecycleConfiguration is not up to date, setting lifeCycleRules");
            s3Client.setBucketLifecycleConfiguration(bucketConfig.getBucketName(), bucketLifecycleConfiguration);
        }
    }

    static Rule getExpirationRule(AmazonBucketConfig amazonBucketConfig) {
        return new Rule()
            .withId(amazonBucketConfig.getBucketName() + "-expiration-id")
            .withFilter(new LifecycleFilter())
            .withStatus(BucketLifecycleConfiguration.ENABLED)
            .withExpirationInDays(amazonBucketConfig.getObjectExpirationDays());
    }

    static boolean ruleEquals(Rule rule, Rule other) {
        return rule.getId().equals(other.getId()) &&
            rule.getStatus().equals(other.getStatus()) &&
            rule.getExpirationInDays() == other.getExpirationInDays();
    }

}
