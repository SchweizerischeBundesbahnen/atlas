package ch.sbb.atlas.api.client.bodi;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class TransportCompanyFeignConfig {

  @Bean
  public Retryer transportCompanyRetryer() {
    return new TransportCompanyRetryer();
  }

  @Bean
  public ErrorDecoder transportCompanyErrorDecoder() {
    return new TransportCompanyRetryErrorDecoder();
  }

}
