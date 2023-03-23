package ch.sbb.atlas.user.administration.mapper;

import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.enumeration.UserAccountStatus;
import com.microsoft.graph.models.User;
import java.util.Collections;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

  public static UserModel userToModel(User user) {
    return UserModel.builder()
        .sbbUserId(user.onPremisesSamAccountName)
        .lastName(user.surname)
        .firstName(user.givenName)
        .mail(user.mail)
        .accountStatus(
            UserAccountStatus.getUserAccountStatusFromBoolean(user.accountEnabled))
        .displayName(user.displayName)
        .permissions(Collections.emptySet())
        .build();
  }


}
