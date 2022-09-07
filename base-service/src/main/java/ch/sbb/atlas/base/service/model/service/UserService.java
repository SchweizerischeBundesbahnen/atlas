package ch.sbb.atlas.base.service.model.service;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public final class UserService {

  public static String getSbbUid() {
    return getAccessToken().getClaimAsString("sbbuid");
  }

  private static Jwt getAccessToken() {
    return (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  private UserService() {
    throw new IllegalStateException();
  }
}
