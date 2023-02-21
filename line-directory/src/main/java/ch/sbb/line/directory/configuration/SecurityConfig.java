package ch.sbb.line.directory.configuration;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.oauth2.jwt.JwtClaimNames.AUD;

import ch.sbb.atlas.base.service.model.configuration.AtlasAccessDeniedHandler;
import ch.sbb.atlas.base.service.model.configuration.Role;
import java.util.List;

import ch.sbb.atlas.user.administration.security.UserAdministrationConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Import(UserAdministrationConfig.class)
@EnableWebSecurity
@Configuration
public class SecurityConfig {

  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
  private String issuerUri;

  @Value("${auth.audience.service-name}")
  private String serviceName;

  @Bean
  protected SecurityFilterChain filterChain(HttpSecurity http, AccessDeniedHandler accessDeniedHandler) throws Exception {
    http
        // CORS: by default Spring uses a bean with the name of corsConfigurationSource: @see ch.sbb.esta.config.CorsConfig
        .cors(withDefaults())

        // for details about stateless authentication see e.g. https://golb.hplar.ch/2019/05/stateless.html
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        .and()

        // @see <a href="https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#jc-authorize-requests">Authorize
        // Requests</a>
        .authorizeHttpRequests(authorizeRequests ->
            authorizeRequests
                .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/static/rest-api.html").permitAll()

                // Method security may also be configured using the annotations <code>@PreAuthorize</code> and
                // <code>@PostAuthorize</code>
                // that permit to set fine grained control using the Spring Expression Language:
                // @see <a href="https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#method-security-expressions">Method
                // Security Expressions</a>
                // In order to use these annotations, you have to enable global-method-security using
                // <code>@EnableGlobalMethodSecurity(prePostEnabled = true)</code>.
                .requestMatchers(HttpMethod.DELETE, "/**").hasRole(Role.ATLAS_ADMIN)
                .anyRequest().authenticated()
        )
        .exceptionHandling()
        .accessDeniedHandler(accessDeniedHandler)
        .and()

        // @see <a href="https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#oauth2resourceserver">OAuth
        // 2.0 Resource Server</a>
        .oauth2ResourceServer()
        .jwt()
        .jwtAuthenticationConverter(jwtAuthenticationConverter());
    return http.build();
  }

  @Bean
  JwtDecoder jwtDecoder() {
    NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);

    OAuth2TokenValidator<Jwt> audienceValidator = new JwtClaimValidator<>(AUD,
        (List<String> aud) -> aud.contains(serviceName));
    OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
    OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer,
        audienceValidator);

    jwtDecoder.setJwtValidator(withAudience);

    return jwtDecoder;
  }

  Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

    // Define the appropriate converter for converting roles/scopes to granted authorities
    // - for Azure AD roles use <code>azureAdRoleConverter()</code>
    // - for Azure AD scopes use <code>new JwtGrantedAuthoritiesConverter()</code>
    converter.setJwtGrantedAuthoritiesConverter(azureAdRoleConverter());

    return converter;
  }

  /**
   * Extracts the roles from an Azure AD token and converts them to granted authorities
   */
  private JwtGrantedAuthoritiesConverter azureAdRoleConverter() {
    JwtGrantedAuthoritiesConverter roleConverter = new JwtGrantedAuthoritiesConverter();
    roleConverter.setAuthorityPrefix(Role.ROLE_PREFIX);
    roleConverter.setAuthoritiesClaimName(Role.ROLES_JWT_KEY);
    return roleConverter;
  }

  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return new AtlasAccessDeniedHandler();
  }
}
