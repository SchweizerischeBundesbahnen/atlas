package ch.sbb.atlas.user.administration.config;

import static org.springframework.security.config.Customizer.withDefaults;

import ch.sbb.atlas.api.user.administration.UserInformationApiV1;
import ch.sbb.atlas.configuration.BaseSecurityConfig;
import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.redact.RedactConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@Import(RedactConfig.class)
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
  private String issuerUri;

  @Value("${auth.audience.service-name}")
  private String serviceName;

  @Bean
  protected SecurityFilterChain filterChain(HttpSecurity http) {
    http
        // CORS: by default Spring uses a bean with the name of corsConfigurationSource: @see ch.sbb.esta.config.CorsConfig
        .cors(withDefaults())

        // for details about stateless authentication see e.g. https://golb.hplar.ch/2019/05/stateless.html
        .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // @see <a href="https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#jc-authorize-requests">Authorize
        // Requests</a>
        .authorizeHttpRequests(authorizeRequests ->
            BaseSecurityConfig.addGenerallyAllowedPaths(authorizeRequests)

                // Method security may also be configured using the annotations <code>@PreAuthorize</code> and
                // <code>@PostAuthorize</code>
                // that permit to set fine grained control using the Spring Expression Language:
                // @see <a href="https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#method-security-expressions">Method
                // Security Expressions</a>
                // In order to use these annotations, you have to enable global-method-security using
                // <code>@EnableGlobalMethodSecurity(prePostEnabled = true)</code>.
                .requestMatchers(HttpMethod.GET, "/static/**").permitAll()
                // Allowed for atlas-internal
                .requestMatchers(HttpMethod.GET, "/v1/users/current",
                    "/v1/users/*/displayname",
                    "/v1/users/display-info",
                    "/v1/users/mail",
                    "/v1/users/*").hasAnyRole(Role.ATLAS_INTERNAL)
                .requestMatchers(HttpMethod.GET, UserInformationApiV1.ALLOWED_FOR_ATLAS_INTERNAL_ROLE.toArray(String[]::new)).hasAnyRole(Role.ATLAS_INTERNAL)
                // Others only for atlas-admin
                .requestMatchers("/**").hasRole(Role.ATLAS_ADMIN)
                .anyRequest().hasRole(Role.ATLAS_ADMIN)
        )

        // @see <a href="https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#oauth2resourceserver">OAuth
        // 2.0 Resource Server</a>
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(BaseSecurityConfig.jwtCustomizer()));
    return http.build();
  }

  @Bean
  JwtDecoder jwtDecoder() {
    return BaseSecurityConfig.buildJwtDecoder(issuerUri, serviceName);
  }

}
