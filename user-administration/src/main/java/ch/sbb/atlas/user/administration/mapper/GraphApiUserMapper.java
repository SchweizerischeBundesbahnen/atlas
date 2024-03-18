package ch.sbb.atlas.user.administration.mapper;

import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.enumeration.UserAccountStatus;
import com.microsoft.graph.models.User;
import java.util.Collections;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GraphApiUserMapper {

  public static UserModel userToModel(User user) {
    return UserModel.builder()
        .sbbUserId(user.getOnPremisesSamAccountName())
        .lastName(user.getSurname())
        .firstName(user.getGivenName())
        .mail(user.getMail())
        .accountStatus(UserAccountStatus.getUserAccountStatusFromBoolean(user.getAccountEnabled()))
        .displayName(user.getDisplayName())
        .permissions(Collections.emptySet())
        .build();
  }

}
