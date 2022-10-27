package ch.sbb.atlas.base.service.model.service;

import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public final class UserService {

  private UserService() {
    throw new IllegalStateException();
  }

  public static String getSbbUid() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof Jwt) {
      return ((Jwt) principal).getClaimAsString("sbbuid");
    } else if (principal instanceof String) {
      return (String) principal;
    }
    throw new IllegalStateException("No Authentication found!");
  }

  public static List<String> getRoles() {
    return getAccessToken().getClaim("roles");
  }

  private static Jwt getAccessToken() {

    return (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
