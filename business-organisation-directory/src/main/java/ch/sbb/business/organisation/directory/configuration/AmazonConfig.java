package ch.sbb.business.organisation.directory.configuration;

import static ch.sbb.atlas.amazon.config.AmazonAtlasConfig.configureAmazonS3Client;

import ch.sbb.atlas.amazon.config.AmazonConfigProps;
import ch.sbb.atlas.amazon.service.AmazonFileStreamingService;
import ch.sbb.atlas.amazon.service.AmazonFileStreamingServiceImpl;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.AmazonServiceImpl;
import ch.sbb.atlas.amazon.service.FileService;
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

  @Bean
  public AmazonFileStreamingService amazonFileStreamingService(AmazonService amazonService, FileService fileService) {
    return new AmazonFileStreamingServiceImpl(amazonService, fileService);
  }
}
