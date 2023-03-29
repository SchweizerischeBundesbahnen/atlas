package ch.sbb.atlas.amazon.config;

import java.util.Map;
import lombok.Data;

@Data
public class AmazonConfigProps {
    private String region;

    private Map<String, AmazonBucketConfig> bucketConfigs;

    @Data
    public static class AmazonBucketConfig {
        private String accessKey;

        private String secretKey;

        private String bucketName;

        private int objectExpirationDays;
    }
}
