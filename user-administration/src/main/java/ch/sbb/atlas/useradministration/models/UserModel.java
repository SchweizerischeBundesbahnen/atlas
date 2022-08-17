package ch.sbb.atlas.useradministration.models;

import ch.sbb.atlas.useradministration.enums.UserAccountStatus;

public record UserModel(String sbbUserId, String lastName, String firstName, String mail,
                        UserAccountStatus accountStatus, String displayName) {

  public static UserAccountStatus getUserAccountStatusFromBoolean(Boolean accountStatus) {
    if (accountStatus == null){
      return null;
    }
    if (accountStatus) {
      return UserAccountStatus.ACTIVE;
    }
    return UserAccountStatus.INACTIVE;
  }

}
