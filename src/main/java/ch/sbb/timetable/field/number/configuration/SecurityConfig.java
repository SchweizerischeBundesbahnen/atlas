package ch.sbb.timetable.field.number.configuration;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.oauth2.jwt.JwtClaimNames.AUD;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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

@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private static final String ROLE_PREFIX = "ROLE_";
  private static final String ROLES_KEY = "roles";

  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
  private String issuerUri;

  @Value("${auth.audience.service-name}")
  private String serviceName;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        // CORS: by default Spring uses a bean with the name of corsConfigurationSource: @see ch.sbb.esta.config.CorsConfig
        .cors(withDefaults())

        // for details about stateless authentication see e.g. https://golb.hplar.ch/2019/05/stateless.html
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        .and()

        // @see <a href="https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#jc-authorize-requests">Authorize Requests</a>
        .authorizeRequests(authorizeRequests ->
            authorizeRequests
                .mvcMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                .mvcMatchers("/swagger-ui/**").permitAll()
                .mvcMatchers("/v3/api-docs/**").permitAll()

                // Method security may also be configured using the annotations <code>@PreAuthorize</code> and <code>@PostAuthorize</code>
                // that permit to set fine grained control using the Spring Expression Language:
                // @see <a href="https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#method-security-expressions">Method Security Expressions</a>
                // In order to use these annotations, you have to enable global-method-security using <code>@EnableGlobalMethodSecurity(prePostEnabled = true)</code>.
                .mvcMatchers(HttpMethod.GET, "/").authenticated()
                .mvcMatchers(HttpMethod.POST, "/").hasAnyRole(Role.LIDI_WRITER, Role.LIDI_ADMIN)
                .mvcMatchers(HttpMethod.PUT, "/").hasAnyRole(Role.LIDI_WRITER, Role.LIDI_ADMIN)
                .mvcMatchers(HttpMethod.DELETE, "/").hasRole(Role.LIDI_ADMIN)
                .anyRequest().authenticated()
        )

        // @see <a href="https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#oauth2resourceserver">OAuth 2.0 Resource Server</a>
        .oauth2ResourceServer()
        .jwt()
        .jwtAuthenticationConverter(jwtAuthenticationConverter());
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
    roleConverter.setAuthorityPrefix(ROLE_PREFIX);
    roleConverter.setAuthoritiesClaimName(ROLES_KEY);
    return roleConverter;
  }
}