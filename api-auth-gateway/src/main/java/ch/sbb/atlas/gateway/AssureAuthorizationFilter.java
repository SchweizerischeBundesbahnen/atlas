package ch.sbb.atlas.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * Required dependencies:
 * <dependency>
 *  <groupId>org.springframework.security</groupId>
 *  <artifactId>spring-security-oauth2-client</artifactId>
 * </dependency>
 * <dependency>
 *  <groupId>org.springframework.boot</groupId>
 *  <artifactId>spring-boot-starter-security</artifactId>
 * </dependency>
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class AssureAuthorizationFilter extends AbstractGatewayFilterFactory<Object> {

  private final TokenService tokenService;

  @Override
  public GatewayFilter apply(Object config) {
    return addAccessTokenIfNotPresent();
  }

  /**
   * GatewayFilter to add an Authorization Header, if currently not set
   */
  private GatewayFilter addAccessTokenIfNotPresent() {
    return (exchange, chain) -> tokenService.getClientCredentialAccessToken().map(accessToken -> {
      if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
        exchange.getRequest()
                .mutate()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();
      }
      return exchange;
    }).defaultIfEmpty(exchange).flatMap(chain::filter);
  }

}