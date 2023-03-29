package ch.sbb.atlas.amazon.service;

import ch.sbb.atlas.amazon.config.AmazonConfigProps.AmazonBucketConfig;
import com.amazonaws.services.s3.AmazonS3;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AmazonBucketClient {
    private final AmazonBucket bucket;
    private final AmazonS3 client;
    private final AmazonBucketConfig amazonBucketConfig;
}
