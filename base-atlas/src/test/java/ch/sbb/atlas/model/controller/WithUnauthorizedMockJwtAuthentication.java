package ch.sbb.atlas.model.controller;

import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.model.controller.WithUnauthorizedMockJwtAuthentication.MockUnauthorizedJwtAuthenticationFactory;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockUnauthorizedJwtAuthenticationFactory.class)
public @interface WithUnauthorizedMockJwtAuthentication {

  String SBB_UID = "e123123";

  String sbbuid() default SBB_UID;

  String name() default "Unauthorized User";

  class MockUnauthorizedJwtAuthenticationFactory implements
      WithSecurityContextFactory<WithUnauthorizedMockJwtAuthentication> {

    @Override
    public SecurityContext createSecurityContext(WithUnauthorizedMockJwtAuthentication annotation) {
      return createSecurityContext(annotation.sbbuid());
    }

    public static SecurityContext createSecurityContext(String sbbuid) {
      SecurityContext context = SecurityContextHolder.createEmptyContext();
      Authentication authentication = new JwtAuthenticationToken(createJwt(sbbuid),
          AuthorityUtils.createAuthorityList(Role.AUTHORITY_UNAUTHORIZED, Role.AUTHORITY_INTERNAL));
      authentication.setAuthenticated(true);
      context.setAuthentication(authentication);
      return context;
    }

    public static Jwt createJwt(String sbbuid) {
      return createJwt(sbbuid, List.of(Role.ATLAS_ROLES_UNAUTHORIZED_KEY, Role.ATLAS_INTERNAL));
    }

    public static Jwt createJwt(String sbbuid, List<String> roles) {
      return Jwt.withTokenValue("token")
          .header("header", "value")
          .claim("sbbuid", sbbuid)
          .claim("roles", roles)
          .audience(Collections.singletonList("87e6e634-6ba1-4e7a-869d-3348b4c3eafc"))
          .issuer(
              "https://login.microsoftonline.com/2cda5d11-f0ac-46b3-967d-af1b2e1bd01a/v2.0")
          .build();
    }

  }
}
