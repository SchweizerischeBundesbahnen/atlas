package ch.sbb.atlas.amazon.config;

import lombok.Data;

@Data
public class AmazonConfigProps {

    private String accessKey;

    private String secretKey;

    private String region;

    private String bucketName;

    private int objectExpirationDays;

}
