package ch.sbb.atlas.service;

import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public final class UserService {

  private UserService() {
    throw new IllegalStateException();
  }

  public static String getSbbUid() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof Jwt jwt) {
      return getSbbuidFromJwt(jwt);
    } else if (principal instanceof String stringAuth) {
      return stringAuth;
    }
    throw new IllegalStateException("No Authentication found!");
  }

  public static List<String> getRoles() {
    return getAccessToken().getClaim("roles");
  }

  public static Jwt getAccessToken() {
    return (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  public static boolean isClientCredentialAuthentication() {
    return getSbbuidFromJwt(getAccessToken()) == null;
  }

  private static String getSbbuidFromJwt(Jwt jwt) {
    return jwt.getClaimAsString("sbbuid");
  }
}
