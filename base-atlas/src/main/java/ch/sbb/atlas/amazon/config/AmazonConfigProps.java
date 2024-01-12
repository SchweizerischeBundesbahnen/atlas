package ch.sbb.atlas.amazon.config;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
