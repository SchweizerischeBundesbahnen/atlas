package ch.sbb.atlas.base.service.model.service;


import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public final class UserService {

  public static String getSbbUid() {
    return getAccessToken().getClaimAsString("sbbuid");
  }

  public static List<String> getRoles() {
    return getAccessToken().getClaim("roles");
  }

  private static Jwt getAccessToken() {
    return (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  private UserService() {
    throw new IllegalStateException();
  }
}
