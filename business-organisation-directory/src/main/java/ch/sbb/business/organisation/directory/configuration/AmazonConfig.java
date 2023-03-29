package ch.sbb.business.organisation.directory.configuration;

import static ch.sbb.atlas.amazon.config.AmazonAtlasConfig.configureAmazonS3Client;

import ch.sbb.atlas.amazon.config.AmazonConfigProps;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.AmazonServiceImpl;
import ch.sbb.atlas.amazon.service.FileServiceImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfig {
    @Bean
    @ConfigurationProperties(prefix = "amazon")
    public AmazonConfigProps amazonConfigProps() {
        return new AmazonConfigProps();
    }

    @Bean
    public AmazonService amazonService() {
        return new AmazonServiceImpl(configureAmazonS3Client(amazonConfigProps()), new FileServiceImpl());
    }
}
