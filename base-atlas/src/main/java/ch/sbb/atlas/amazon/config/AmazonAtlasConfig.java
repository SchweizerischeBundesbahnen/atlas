package ch.sbb.atlas.amazon.config;

import ch.sbb.atlas.amazon.config.AmazonConfigProps.AmazonBucketConfig;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonBucketClient;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.BucketLifecycleConfiguration;
import software.amazon.awssdk.services.s3.model.ExpirationStatus;
import software.amazon.awssdk.services.s3.model.GetBucketLifecycleConfigurationRequest;
import software.amazon.awssdk.services.s3.model.LifecycleExpiration;
import software.amazon.awssdk.services.s3.model.LifecycleRule;
import software.amazon.awssdk.services.s3.model.LifecycleRuleFilter;
import software.amazon.awssdk.services.s3.model.PutBucketLifecycleConfigurationRequest;

@UtilityClass
@Slf4j
public class AmazonAtlasConfig {

  public static List<AmazonBucketClient> configureAmazonS3Client(AmazonConfigProps props) {
    Map<String, AmazonBucketConfig> amazonBucketConfig = props.getBucketConfigs();

    return amazonBucketConfig.entrySet().stream().map(entry -> {
      AmazonBucketConfig bucketConfig = entry.getValue();
      S3ClientBuilder s3ClientBuilder = S3Client.builder()
          .region(Region.of(props.getRegion()))
          .forcePathStyle(bucketConfig.isForcePathStyle());

      AwsCredentials awsCredentials = AwsBasicCredentials.create(bucketConfig.getAccessKey(), bucketConfig.getSecretKey());
      s3ClientBuilder.credentialsProvider(StaticCredentialsProvider.create(awsCredentials));

      if (bucketConfig.getEndpoint() != null) {
        s3ClientBuilder.endpointOverride(URI.create(bucketConfig.getEndpoint()));
      }

      S3Client s3Client = s3ClientBuilder.build();

      if (bucketConfig.getObjectExpirationDays() != null) {
        setBucketLifecycleConfiguration(bucketConfig, s3Client);
      }

      return new AmazonBucketClient(AmazonBucket.fromProperty(entry.getKey()), s3Client, bucketConfig);
    }).toList();
  }

  static void setBucketLifecycleConfiguration(AmazonBucketConfig bucketConfig, S3Client s3Client) {
    LifecycleRule lifeCycleRules = getExpirationRule(bucketConfig);

    List<LifecycleRule> currentConfig =
        s3Client.getBucketLifecycleConfiguration(
            GetBucketLifecycleConfigurationRequest.builder().bucket(bucketConfig.getBucketName()).build()).rules();
    if (!currentConfig.stream().allMatch(rule -> ruleEquals(rule, lifeCycleRules))) {
      log.info("Current BucketLifecycleConfiguration is not up to date, setting lifeCycleRules");
      s3Client.putBucketLifecycleConfiguration(PutBucketLifecycleConfigurationRequest.builder()
          .bucket(bucketConfig.getBucketName())
          .lifecycleConfiguration(BucketLifecycleConfiguration.builder()
              .rules(lifeCycleRules)
              .build())
          .build());
    }
  }

  private static LifecycleRule getExpirationRule(AmazonBucketConfig amazonBucketConfig) {
    return LifecycleRule.builder()
        .id(amazonBucketConfig.getBucketName() + "-expiration-id")
        .filter(LifecycleRuleFilter.builder().build())
        .status(ExpirationStatus.ENABLED)
        .expiration(LifecycleExpiration.builder().days(amazonBucketConfig.getObjectExpirationDays()).build())
        .build();
  }

  private static boolean ruleEquals(LifecycleRule rule, LifecycleRule other) {
    return rule.id().equals(other.id()) &&
        rule.status().equals(other.status()) &&
        Objects.equals(rule.expiration().days(), other.expiration().days());
  }

}
