package ch.sbb.atlas.configuration;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Role {

  public static final String ROLE_PREFIX = "ROLE_";
  public static final String ROLES_JWT_KEY = "roles";
  public static final String ATLAS_ADMIN =  "atlas-admin";

  // Use this when using @Secured
  public static final String SECURED_FOR_ATLAS_ADMIN =  Role.ROLE_PREFIX + Role.ATLAS_ADMIN;

}
