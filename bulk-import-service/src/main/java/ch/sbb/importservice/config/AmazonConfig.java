package ch.sbb.importservice.config;

import static ch.sbb.atlas.s3.config.AmazonAtlasConfig.configureAmazonS3Client;

import ch.sbb.atlas.s3.config.AmazonConfigProps;
import ch.sbb.atlas.s3.service.AmazonService;
import ch.sbb.atlas.s3.service.AmazonServiceImpl;
import ch.sbb.atlas.s3.service.FileService;
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
  public AmazonService amazonService(FileService fileService) {
    return new AmazonServiceImpl(configureAmazonS3Client(amazonConfigProps()), fileService);
  }
}
