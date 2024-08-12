package ch.sbb.atlas.amazon.config;

import ch.sbb.atlas.amazon.config.AmazonConfigProps.AmazonBucketConfig;
import java.util.List;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketLifecycleConfiguration;
import software.amazon.awssdk.services.s3.model.ExpirationStatus;
import software.amazon.awssdk.services.s3.model.GetBucketLifecycleConfigurationRequest;
import software.amazon.awssdk.services.s3.model.LifecycleExpiration;
import software.amazon.awssdk.services.s3.model.LifecycleRule;
import software.amazon.awssdk.services.s3.model.LifecycleRuleFilter;
import software.amazon.awssdk.services.s3.model.PutBucketLifecycleConfigurationRequest;

@Slf4j
@UtilityClass
public class AmazonLifecycleRuleConfigurer {

  public static void setBucketLifecycleConfiguration(AmazonBucketConfig bucketConfig, S3Client s3Client) {
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

  static LifecycleRule getExpirationRule(AmazonBucketConfig amazonBucketConfig) {
    return LifecycleRule.builder()
        .id(amazonBucketConfig.getBucketName() + "-expiration-id")
        .filter(LifecycleRuleFilter.builder().build())
        .status(ExpirationStatus.ENABLED)
        .expiration(LifecycleExpiration.builder().days(amazonBucketConfig.getObjectExpirationDays()).build())
        .build();
  }

  static boolean ruleEquals(LifecycleRule rule, LifecycleRule other) {
    return rule.id().equals(other.id()) &&
        rule.status().equals(other.status()) &&
        Objects.equals(rule.expiration().days(), other.expiration().days());
  }

}
