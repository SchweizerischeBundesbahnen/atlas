package ch.sbb.business.organisation.directory.configuration;

import ch.sbb.atlas.amazon.config.AmazonAtlasConfig;
import ch.sbb.atlas.amazon.config.AmazonConfigProps;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.AmazonServiceImpl;
import ch.sbb.atlas.amazon.service.FileServiceImpl;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfig {

    public static final String EXPORT_FILES = "export-files";
    @Value("${amazon.bucketConfigs.export-files.bucketName}")
    private String bucketName;
    @Bean
    @ConfigurationProperties(prefix = "amazon")
    public AmazonConfigProps amazonConfigProps() {
        return new AmazonConfigProps();
    }

    @Bean
    public AmazonS3 getAmazonS3Client() {
        return AmazonAtlasConfig.configureAmazonS3Client(amazonConfigProps(), EXPORT_FILES);
    }

    @Bean
    public AmazonService amazonService() {
        return new AmazonServiceImpl(this.getAmazonS3Client(), new FileServiceImpl(),  bucketName);
    }

}
