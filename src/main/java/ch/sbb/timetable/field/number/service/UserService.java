package ch.sbb.timetable.field.number.service;


import java.util.Map;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class UserService {

  public static String getSbbUid() {
    return getAccessToken().getClaimAsString("sbbuid");
  }

  public static Map<String, Object> getClaims() {
    return getAccessToken().getClaims();
  }

  private static Jwt getAccessToken() {
    return ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
  }

  private UserService() {
    throw new IllegalStateException();
  }
}
