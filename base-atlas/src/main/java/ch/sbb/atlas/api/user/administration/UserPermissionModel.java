package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "UserPermission")
public class UserPermissionModel extends BaseVersionModel {

  @NotNull
  private ApplicationRole role;

  @NotNull
  private ApplicationType application;

  @Schema(description = "Permission Restrictions")
  @NotNull
  @Builder.Default
  private List<? extends PermissionRestrictionModel<?>> permissionRestrictions = new ArrayList<>();

}
