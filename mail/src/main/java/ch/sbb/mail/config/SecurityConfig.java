package ch.sbb.mail.config;

import static org.springframework.security.config.Customizer.withDefaults;

import ch.sbb.atlas.configuration.BaseSecurityConfig;
import ch.sbb.atlas.configuration.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
  private String issuerUri;

  @Value("${auth.audience.service-name}")
  private String serviceName;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(withDefaults())
        .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorizeRequests ->
            BaseSecurityConfig.addGenerallyAllowedPaths(authorizeRequests)
                .requestMatchers(HttpMethod.POST, "/internal/send").hasRole(Role.ATLAS_ADMIN)
                .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(BaseSecurityConfig.jwtCustomizer()));
    return http.build();
  }

  @Bean
  JwtDecoder jwtDecoder() {
    return BaseSecurityConfig.buildJwtDecoder(issuerUri, serviceName);
  }

}
