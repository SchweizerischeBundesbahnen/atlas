package ch.sbb.atlas.amazon.config;

import ch.sbb.atlas.amazon.config.AmazonConfigProps.AmazonBucketConfig;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonBucketClient;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@UtilityClass
public class AmazonAtlasConfig {

  public static List<AmazonBucketClient> configureAmazonS3Client(AmazonConfigProps props) {
    Map<String, AmazonBucketConfig> amazonBucketConfig = props.getBucketConfigs();

    return amazonBucketConfig.entrySet().stream().map(entry -> {
      AmazonBucketConfig bucketConfig = entry.getValue();
      AwsCredentials awsCredentials = AwsBasicCredentials.create(bucketConfig.getAccessKey(), bucketConfig.getSecretKey());
      S3Client s3Client = S3Client.builder()
          .region(Region.of(props.getRegion()))
          .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
          .build();

      AmazonLifecycleRuleConfigurer.setBucketLifecycleConfiguration(bucketConfig, s3Client);

      return new AmazonBucketClient(AmazonBucket.fromProperty(entry.getKey()), s3Client, bucketConfig);
    }).toList();
  }

}
