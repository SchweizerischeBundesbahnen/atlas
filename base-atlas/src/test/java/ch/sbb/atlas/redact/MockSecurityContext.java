package ch.sbb.atlas.redact;

import static org.mockito.Mockito.when;

import ch.sbb.atlas.configuration.Role;
import java.util.List;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class MockSecurityContext {

  public static void setSecurityContextToUnauthorized() {
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Authentication unauthorizedAuthentication = getUnauthorizedAuthentication();
    when(securityContext.getAuthentication()).thenReturn(unauthorizedAuthentication);
    SecurityContextHolder.setContext(securityContext);
  }

  private static Authentication getUnauthorizedAuthentication() {
    Authentication authentication = Mockito.mock(Authentication.class);
    Jwt jwt = Mockito.mock(Jwt.class);
    when(jwt.getClaim(Role.ROLES_JWT_KEY)).thenReturn(List.of(Role.ATLAS_ROLES_UNAUTHORIZED_KEY));
    when(authentication.getPrincipal()).thenReturn(jwt);
    return authentication;
  }

  public static void setSecurityContextToAuthorized() {
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Authentication authorizedAuthentication = getAuthorizedAuthentication();
    when(securityContext.getAuthentication()).thenReturn(authorizedAuthentication);
    SecurityContextHolder.setContext(securityContext);
  }

  private static Authentication getAuthorizedAuthentication() {
    Authentication authentication = Mockito.mock(Authentication.class);
    Jwt jwt = Mockito.mock(Jwt.class);
    when(jwt.getClaim(Role.ROLES_JWT_KEY)).thenReturn(List.of("Role1"));
    when(authentication.getPrincipal()).thenReturn(jwt);
    return authentication;
  }

}
