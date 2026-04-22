package ch.sbb.atlas.model.controller;

import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockJwtAuthenticationFactory;
import ch.sbb.atlas.service.UserService;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.Jwt.Builder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockJwtAuthenticationFactory.class)
public @interface WithMockJwtAuthentication {

  String MOCKUSER_SBB_UID = "e123456";

  String sbbuid() default MOCKUSER_SBB_UID;

  MockRole role() default MockRole.ATLAS_ADMIN;

  MockUser user() default MockUser.USER;

  MockAccountType accountType() default MockAccountType.STANDARD;

  class MockJwtAuthenticationFactory implements WithSecurityContextFactory<WithMockJwtAuthentication> {

    @Override
    public @NonNull SecurityContext createSecurityContext(@NonNull WithMockJwtAuthentication annotation) {
      SecurityContext context = SecurityContextHolder.createEmptyContext();
      Authentication authentication = createAuthenticationToken(annotation);
      authentication.setAuthenticated(true);
      context.setAuthentication(authentication);
      return context;
    }

    private JwtAuthenticationToken createAuthenticationToken(WithMockJwtAuthentication annotation) {
      Jwt jwt = createJwt(annotation);

      List<String> roleClaims = jwt.getClaim(Role.ROLES_JWT_KEY);
      List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(
          roleClaims.stream().map(i -> Role.ROLE_PREFIX + i).toList());
      return new JwtAuthenticationToken(jwt, authorityList);
    }

    private static Jwt createJwt(WithMockJwtAuthentication annotation) {
      Builder jwtBuilder = Jwt.withTokenValue("token")
          .header("header", "value")
          .audience(Collections.singletonList("87e6e634-6ba1-4e7a-869d-3348b4c3eafc"))
          .issuer("https://login.microsoftonline.com/2cda5d11-f0ac-46b3-967d-af1b2e1bd01a/v2.0")
          .claim(Role.ROLES_JWT_KEY, annotation.role().getRoleClaims());

      if (annotation.role() == MockRole.UNAUTHORIZED) {
        jwtBuilder.claim(UserService.AZP_CLAIM, "unauthorized-client-id");
      } else {
        switch (annotation.user()) {
          case USER -> {
            jwtBuilder.claim(UserService.SBBUID_CLAIM, annotation.sbbuid());
            jwtBuilder.claim("name", "Test User");

            switch (annotation.accountType()) {
              case STANDARD -> jwtBuilder.claim(UserService.PREFERRED_USERNAME_CLAIM, "test.user@sbb.ch");
              case GUEST -> jwtBuilder.claim(UserService.PREFERRED_USERNAME_CLAIM, annotation.sbbuid() + "@sbb.ch");
            }
          }
          case CLIENT_CREDENTIAL -> jwtBuilder.claim(UserService.AZP_CLAIM, "client-id");
        }
      }
      return jwtBuilder.build();
    }

  }

  @Getter
  @RequiredArgsConstructor
  enum MockRole {
    ATLAS_ADMIN(List.of(Role.ATLAS_ADMIN, Role.ATLAS_INTERNAL)),
    STANDARD(List.of(Role.ATLAS_INTERNAL)),
    UNAUTHORIZED(List.of(Role.ATLAS_ROLES_UNAUTHORIZED_KEY, Role.ATLAS_INTERNAL)),
    NONE(Collections.emptyList());

    private final List<String> roleClaims;
  }

  enum MockUser {
    USER,
    CLIENT_CREDENTIAL,
  }

  enum MockAccountType {
    STANDARD,
    GUEST,
  }
}
