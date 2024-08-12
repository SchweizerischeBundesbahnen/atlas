package ch.sbb.atlas.amazon.service;

import ch.sbb.atlas.amazon.config.AmazonConfigProps.AmazonBucketConfig;
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
