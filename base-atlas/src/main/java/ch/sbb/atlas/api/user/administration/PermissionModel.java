package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
@Schema(name = "Permission")
public class PermissionModel extends BaseVersionModel {

  @NotNull
  private ApplicationRole role;

  @NotNull
  private ApplicationType application;

  @Schema(description = "Permission Restrictions")
  @NotNull
  @Builder.Default
  private List<? extends PermissionRestrictionModel<?>> permissionRestrictions = new ArrayList<>();

  @Schema(hidden = true)
  @JsonIgnore
  @AssertTrue(message = "Only one restriction is allowed, and its only for SEPODI.")
  public boolean isNovaOrInfoPlusPermissionAllowed() {

    boolean hasInfoPlus = getPermissionRestrictions().stream()
        .anyMatch(r -> r.getType() == PermissionRestrictionType.INFO_PLUS_TERMINATION_VOTE
            && Boolean.TRUE.equals(r.getValue()));

    boolean hasNova = getPermissionRestrictions().stream()
        .anyMatch(r -> r.getType() == PermissionRestrictionType.NOVA_TERMINATION_VOTE
            && Boolean.TRUE.equals(r.getValue()));

    if (getApplication() == ApplicationType.SEPODI) {
      return !(hasInfoPlus && hasNova);
    } else {
      return getPermissionRestrictions().stream()
          .noneMatch(r ->
              r.getType() == PermissionRestrictionType.INFO_PLUS_TERMINATION_VOTE
                  || r.getType() == PermissionRestrictionType.NOVA_TERMINATION_VOTE
          );
    }
  }

  @Schema(hidden = true)
  @JsonIgnore
  @AssertTrue(message = "Restrictions must be allowed")
  public boolean isAllRestrictionTypesAllowed() {
    Set<PermissionRestrictionType> allowedRestrictionTypes = AllowedPermissionRestrictions.get(application, role);
    Set<PermissionRestrictionType> permissionRestrictionTypes = permissionRestrictions.stream()
        .map(PermissionRestrictionModel::getType)
        .collect(Collectors.toSet());

    return allowedRestrictionTypes.containsAll(permissionRestrictionTypes);
  }

}
