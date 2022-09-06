package ch.sbb.atlas.user.administration.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum UserAccountStatus {
  ACTIVE,INACTIVE,DELETED;

  public static UserAccountStatus getUserAccountStatusFromBoolean(Boolean accountStatus) {
    if (accountStatus == null || !accountStatus) {
      return UserAccountStatus.INACTIVE;
    }
    return UserAccountStatus.ACTIVE;
  }
}
