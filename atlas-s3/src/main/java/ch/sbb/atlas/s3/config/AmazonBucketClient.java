package ch.sbb.atlas.s3.config;

import ch.sbb.atlas.s3.config.AmazonConfigProps.AmazonBucketConfig;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;

@Data
@RequiredArgsConstructor
public class AmazonBucketClient {
    private final AmazonBucket bucket;
    private final S3Client client;
    private final AmazonBucketConfig amazonBucketConfig;
}
