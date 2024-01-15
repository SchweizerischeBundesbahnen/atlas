package ch.sbb.atlas.amazon.config;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.amazon.config.AmazonConfigProps.AmazonBucketConfig;
import ch.sbb.atlas.amazon.service.AmazonBucketClient;
import ch.sbb.atlas.model.controller.IntegrationTest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

@IntegrationTest
class AmazonAtlasConfigTest {

  @Value("${AMAZON_S3_ACCESS_KEY}")
  private String accessKey;

  @Value("${AMAZON_S3_SECRET_KEY}")
  private String secretKey;

  @Test
  void shouldConfigureS3Client() {
    Map<String, AmazonBucketConfig> bucketConfigs = new HashMap<>();
    bucketConfigs.put("export-files",
        AmazonBucketConfig.builder().accessKey(accessKey).secretKey(secretKey).bucketName("atlas-data-export-dev-dev")
            .objectExpirationDays(30).build());

    List<AmazonBucketClient> amazonBucketClients = AmazonAtlasConfig.configureAmazonS3Client(
        AmazonConfigProps.builder().region("eu-central-1").bucketConfigs(bucketConfigs).build());

    assertThat(amazonBucketClients).hasSize(1);
  }
}