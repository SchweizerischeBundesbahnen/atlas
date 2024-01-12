package ch.sbb.atlas.amazon.config;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AmazonConfigProps {
    private String region;

    private Map<String, AmazonBucketConfig> bucketConfigs;

    @Data
    @Builder
    public static class AmazonBucketConfig {
        private String accessKey;

        private String secretKey;

        private String bucketName;

        private int objectExpirationDays;
    }
}
