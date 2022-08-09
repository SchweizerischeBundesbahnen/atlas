package ch.sbb.line.directory.configuration;

import ch.sbb.atlas.amazon.config.AmazonAtlasConfig;
import ch.sbb.atlas.amazon.controller.AmazonController;
import ch.sbb.atlas.amazon.controller.AmazonControllerImpl;
import ch.sbb.atlas.amazon.service.FileService;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfig {

  @Value("${amazon.accessKey}")
  private String accessKey;

  @Value("${amazon.secretKey}")
  private String secretKey;

  @Value("${amazon.region}")
  private String region;

  @Bean
  public AmazonS3 getAmazonS3Client() {
    return AmazonAtlasConfig.configureAmazonS3Client(accessKey,secretKey,region);
  }

  @Bean
  public AmazonController amazonController(){
    return new AmazonControllerImpl(this.getAmazonS3Client(),new FileService());
  }

}
