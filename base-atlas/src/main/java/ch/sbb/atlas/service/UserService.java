package ch.sbb.atlas.service;

import ch.sbb.atlas.configuration.Role;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@UtilityClass
public final class UserService {

  public static final String SBBUID_CLAIM = "sbbuid";
  static final String AZP_CLAIM = "azp";

  public static Jwt getAccessToken() {
    return (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  public static List<String> getRoles() {
    return getAccessToken().getClaim(Role.ROLES_JWT_KEY);
  }

  public static boolean hasUnauthorizedRole() {
    return getRoles().contains(Role.ATLAS_ROLES_UNAUTHORIZED_KEY);
  }

  public static boolean isClientCredentialAuthentication(Jwt jwt) {
    return getSbbuidClaimFromJwt(jwt) == null;
  }

  public static String getUserIdentifier() {
    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
        .map(Authentication::getPrincipal)
        .map(principal -> switch (principal) {
          case Jwt jwt -> getUserIdentifierOnJwtAuthentication(jwt);
          case String stringAuth -> stringAuth;
          default -> null;
        })
        .orElseThrow(() -> new IllegalStateException("No Authentication found!"));
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
