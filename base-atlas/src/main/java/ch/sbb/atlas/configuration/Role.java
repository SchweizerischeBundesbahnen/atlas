package ch.sbb.atlas.configuration;

import lombok.experimental.UtilityClass;

/**
 * Roles defined on the atlas api:
 * - atlas-admin:
 *    Highest privileges. May perform all operations on atlas.
 * - atlas-internal:
 *    Any authenticated SBB user, as well as ClientCredential clients used by atlas
 * - Unauthorized
 *    Used by the api-auth-gateway to allow the atlas frontend to be publicly available.
 */
@UtilityClass
public class Role {

  public static final String ROLE_PREFIX = "ROLE_";
  public static final String ROLES_JWT_KEY = "roles";
  public static final String ATLAS_ROLES_UNAUTHORIZED_KEY = "Unauthorized";
  public static final String AUTHORITY_UNAUTHORIZED = Role.ROLE_PREFIX + Role.ATLAS_ROLES_UNAUTHORIZED_KEY;
  public static final String ATLAS_ADMIN = "atlas-admin";
  public static final String ATLAS_INTERNAL = "atlas-internal";
  public static final String AUTHORITY_INTERNAL = Role.ROLE_PREFIX + Role.ATLAS_INTERNAL;

  // Use this when using @Secured
  public static final String SECURED_FOR_ATLAS_ADMIN = Role.ROLE_PREFIX + Role.ATLAS_ADMIN;

}
