package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.redact.Redacted;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
@Redacted
@Schema(name = "UserDisplayName")
public class UserDisplayNameModel {

  @Schema(description = "SBB User Id", example = "u111111")
  private String sbbUserId;

  @Redacted
  @Schema(description = "User display name (azure). Falls back to the requested user id if it can not be resolved.",
      example = "Example User (IT-PTR-CEN2-YPT)")
  private String displayName;

  public static UserDisplayNameModel toModel(UserModel userModel) {
    return UserDisplayNameModel.builder()
        .sbbUserId(userModel.getSbbUserId())
        .displayName(userModel.getDisplayName())
        .build();
  }
}
