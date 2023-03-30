package ch.sbb.atlas.service;

import ch.sbb.atlas.configuration.Role;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@UtilityClass
public final class UserService {

  static final String SBBUID_CLAIM = "sbbuid";
  static final String AZP_CLAIM = "azp";

  public static Jwt getAccessToken() {
    return (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  public static List<String> getRoles() {
    return getAccessToken().getClaim(Role.ROLES_JWT_KEY);
  }

  public static boolean isClientCredentialAuthentication(Jwt jwt) {
    return getSbbuidClaimFromJwt(jwt) == null;
  }

  public static String getUserIdentifier() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof Jwt jwt) {
      return getUserIdentifierOnJwtAuthentication(jwt);
    } else if (principal instanceof String stringAuth) {
      return stringAuth;
    }
    throw new IllegalStateException("No Authentication found!");
  }

  private static String getUserIdentifierOnJwtAuthentication(Jwt jwt) {
    if (isClientCredentialAuthentication(jwt)) {
      return getAzpClaimFromJwt(jwt);
    }
    return getSbbuidClaimFromJwt(jwt);
  }

  private static String getSbbuidClaimFromJwt(Jwt jwt) {
    return jwt.getClaimAsString(SBBUID_CLAIM);
  }

  /**
   * azp = client_id of the authorized presenter Also see
   * <a href="https://developers.google.com/identity/openid-connect/openid-connect">OpenId Connect at developers.google.com</a>
   */
  private static String getAzpClaimFromJwt(Jwt jwt) {
    return jwt.getClaimAsString(AZP_CLAIM);
  }
}
