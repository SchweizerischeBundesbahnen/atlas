package ch.sbb.atlas.api.user.administration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@Schema(name = "UserPermissionCreate")
public class UserPermissionCreateModel {

  @Schema(description = "SBB User-ID", example = "u123456")
  @NotEmpty
  private String sbbUserId;

}
