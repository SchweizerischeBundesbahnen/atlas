package ch.sbb.atlas.configuration;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

@UtilityClass
public class BaseSecurityConfig {

  public static AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry addGenerallyAllowedPaths(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizeRequests) {
    return addAllowedActuatorPaths(authorizeRequests)
        .requestMatchers("/swagger-ui/**").permitAll()
        .requestMatchers("/v3/api-docs/**").permitAll()
        .requestMatchers("/static/rest-api.html").permitAll();
  }

  public static AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry addAllowedActuatorPaths(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizeRequests) {
    return authorizeRequests
        .requestMatchers(HttpMethod.GET, "/actuator/health/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/actuator/info/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/actuator/metrics/**").permitAll();
  }

}
