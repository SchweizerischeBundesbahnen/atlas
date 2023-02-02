package ch.sbb.importservice.config;

import ch.sbb.atlas.base.service.amazon.config.AmazonAtlasConfig;
import ch.sbb.atlas.base.service.amazon.config.AmazonConfigProps;
import ch.sbb.atlas.base.service.amazon.service.AmazonService;
import ch.sbb.atlas.base.service.amazon.service.AmazonServiceImpl;
import ch.sbb.atlas.base.service.amazon.service.FileServiceImpl;
import com.amazonaws.services.s3.AmazonS3;
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
  public AmazonS3 getAmazonS3Client() {
    return AmazonAtlasConfig.configureAmazonS3Client(amazonConfigProps());
  }

  @Bean
  public AmazonService amazonService() {
    return new AmazonServiceImpl(this.getAmazonS3Client(), new FileServiceImpl());
  }

}
