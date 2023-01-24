package ch.sbb.workflow.config;

import ch.sbb.atlas.base.service.model.service.UserService;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
@RequiredArgsConstructor
public class TokenPassingFeignConfig {

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> requestTemplate
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + UserService.getAccessToken().getTokenValue());
  }

}