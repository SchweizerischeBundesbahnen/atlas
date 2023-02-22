package ch.sbb.atlas.api.user.administration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(name = "UserDisplayName")
public class UserDisplayNameModel {

  @Schema(description = "User display name (azure)", example = "Example User (IT-PTR-CEN2-YPT)")
  private String displayName;

  public static UserDisplayNameModel toModel(UserModel userModel) {
    return UserDisplayNameModel.builder()
        .displayName(userModel.getDisplayName())
        .build();
  }
}
