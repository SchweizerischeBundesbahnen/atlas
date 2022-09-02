package ch.sbb.atlas.user.administration.enums;

public enum UserAccountStatus {
  ACTIVE,INACTIVE,DELETED;

  public static UserAccountStatus getUserAccountStatusFromBoolean(Boolean accountStatus) {
    if (accountStatus == null || !accountStatus) {
      return UserAccountStatus.INACTIVE;
    }
    return UserAccountStatus.ACTIVE;
  }
}
