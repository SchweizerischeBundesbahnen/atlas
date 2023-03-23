package ch.sbb.atlas.api.user.administration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Schema(name = "UserPermissionCreate")
public class UserPermissionCreateModel extends PermissionCreateModel {

  @Schema(description = "SBB User-ID", example = "u123456")
  @NotEmpty
  private String sbbUserId;

}
