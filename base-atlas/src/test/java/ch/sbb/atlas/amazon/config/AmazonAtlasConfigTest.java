package ch.sbb.atlas.amazon.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.config.AmazonConfigProps.AmazonBucketConfig;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ExpirationStatus;
import software.amazon.awssdk.services.s3.model.GetBucketLifecycleConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketLifecycleConfigurationResponse;
import software.amazon.awssdk.services.s3.model.LifecycleRule;
import software.amazon.awssdk.services.s3.model.PutBucketLifecycleConfigurationRequest;

class AmazonAtlasConfigTest {

  @Mock
  private S3Client s3Client;

  @Captor
  ArgumentCaptor<PutBucketLifecycleConfigurationRequest> putBucketLifecycleConfigurationRequestCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void shouldSetBucketLifecycleConfiguration() {
    AmazonBucketConfig amazonBucketConfig = AmazonBucketConfig.builder()
        .bucketName("bucket")
        .objectExpirationDays(30)
        .build();

    when(s3Client.getBucketLifecycleConfiguration(any(GetBucketLifecycleConfigurationRequest.class))).thenReturn(
        GetBucketLifecycleConfigurationResponse.builder().rules(LifecycleRule.builder().id("id1").build()).build());

    // when
    AmazonAtlasConfig.setBucketLifecycleConfiguration(amazonBucketConfig, s3Client);

    // then
    verify(s3Client).putBucketLifecycleConfiguration(putBucketLifecycleConfigurationRequestCaptor.capture());

    List<LifecycleRule> rules = putBucketLifecycleConfigurationRequestCaptor.getValue().lifecycleConfiguration().rules();
    assertThat(rules).hasSize(1);
    LifecycleRule rule = rules.getFirst();
    assertThat(rule.id()).isEqualTo("bucket-expiration-id");
    assertThat(rule.status()).isEqualTo(ExpirationStatus.ENABLED);
    assertThat(rule.expiration().days()).isEqualTo(30);
  }
}