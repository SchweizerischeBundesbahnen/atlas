package ch.sbb.atlas.user.administration.models;

import ch.sbb.atlas.user.administration.enums.UserAccountStatus;
import com.microsoft.graph.models.User;
import lombok.Builder;

@Builder
public record UserModel(String sbbUserId, String lastName, String firstName, String mail,
                        UserAccountStatus accountStatus, String displayName) {
  public static UserModel toModel(User user) {
    return UserModel.builder()
                    .sbbUserId(user.onPremisesSamAccountName)
                    .lastName(user.surname)
                    .firstName(user.givenName)
                    .mail(user.mail)
                    .accountStatus(UserAccountStatus.getUserAccountStatusFromBoolean(user.accountEnabled))
                    .displayName(user.displayName)
                    .build();
  }
}
