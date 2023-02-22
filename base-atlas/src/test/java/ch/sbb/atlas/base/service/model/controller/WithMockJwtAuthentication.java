package ch.sbb.atlas.base.service.model.controller;

import ch.sbb.atlas.base.service.model.controller.WithMockJwtAuthentication.MockJwtAuthenticationFactory;
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
@WithSecurityContext(factory = MockJwtAuthenticationFactory.class)
public @interface WithMockJwtAuthentication {

  String SBB_UID = "e123456";

  String sbbuid() default SBB_UID;

  String name() default "Rob Winch";

  class MockJwtAuthenticationFactory implements
      WithSecurityContextFactory<WithMockJwtAuthentication> {

    @Override
    public SecurityContext createSecurityContext(WithMockJwtAuthentication annotation) {
      SecurityContext context = SecurityContextHolder.createEmptyContext();
      Authentication authentication = new JwtAuthenticationToken(createJwt(annotation.sbbuid()),
          AuthorityUtils.createAuthorityList("ROLE_atlas-admin"));
      authentication.setAuthenticated(true);
      context.setAuthentication(authentication);
      return context;
    }

    public static Jwt createJwt(String sbbuid) {
      return Jwt.withTokenValue("token")
                .header("header", "value")
                .claim("sbbuid", sbbuid)
                .claim("roles", List.of("atlas-admin"))
                .audience(Collections.singletonList("87e6e634-6ba1-4e7a-869d-3348b4c3eafc"))
                .issuer(
                    "https://login.microsoftonline.com/2cda5d11-f0ac-46b3-967d-af1b2e1bd01a/v2.0")
                .build();
    }
  }
}