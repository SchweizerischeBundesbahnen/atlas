package ch.sbb.timetable.field.number;

import ch.sbb.timetable.field.number.WithMockJwtAuthentication.MockJwtAuthenticationFactory;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
      context.setAuthentication(new JwtAuthenticationToken(Jwt.withTokenValue("token")
                                                              .header("header", "value")
                                                              .claim("sbbuid", annotation.sbbuid())
                                                              .build()));
      return context;
    }
  }
}