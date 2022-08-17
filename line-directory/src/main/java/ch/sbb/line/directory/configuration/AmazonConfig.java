package ch.sbb.line.directory.configuration;

import ch.sbb.atlas.amazon.config.AmazonAtlasConfig;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.AmazonServiceImpl;
import ch.sbb.atlas.amazon.service.FileService;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfig {

  @Value("${amazon.accessKey}")
  private String accessKey;

  @Value("${amazon.secretKey}")
  private String secretKey;

  @Value("${amazon.region}")
  private String region;

  @Value("${amazon.bucket.name}")
  private String bucketName;

  @Value("${amazon.bucket.object-expiration-days}")
  private int objectExpirationDays;

  @Bean
  public AmazonS3 getAmazonS3Client() {
    AmazonS3 s3Client = AmazonAtlasConfig.configureAmazonS3Client(accessKey, secretKey, region);
    s3Client
        .setBucketLifecycleConfiguration(bucketName,
            new BucketLifecycleConfiguration()
                .withRules(
                    new BucketLifecycleConfiguration.Rule()
                        .withId(bucketName + "-expiration-id")
                        .withFilter(new LifecycleFilter())
                        .withStatus(BucketLifecycleConfiguration.ENABLED)
                        .withExpirationInDays(objectExpirationDays)
                )
        );
    return s3Client;
  }

  @Bean
  public AmazonService amazonService() {
    return new AmazonServiceImpl(this.getAmazonS3Client(), new FileService());
  }

}
