package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class PermissionCreateModel {

  @Schema(description = "User permissions")
  @NotNull
  @Size(min = 1)
  private List<@Valid @NotNull PermissionModel> permissions;

  @Schema(hidden = true)
  @JsonIgnore
  @AssertTrue(message = "ApplicationType must be unique in permissions")
  boolean isApplicationTypeUniqueInPermissions() {
    Set<ApplicationType> applicationTypesInPermissions = new HashSet<>();
    permissions.forEach(
        permission -> applicationTypesInPermissions.add(permission.getApplication()));
    return applicationTypesInPermissions.size() == permissions.size();
  }

  @Schema(hidden = true)
  @JsonIgnore
  @AssertTrue(message = "Only one restriction is allowed, and its only for SEPODI.")
  public boolean isNovaOrInfoPlusPermissionAllowed() {
    return permissions.stream().allMatch(permission -> {

      boolean hasInfoPlus = permission.getPermissionRestrictions().stream()
          .anyMatch(r -> r.getType() == PermissionRestrictionType.INFO_PLUS_TERMINATION_VOTE
              && Boolean.TRUE.equals(r.getValue()));

      boolean hasNova = permission.getPermissionRestrictions().stream()
          .anyMatch(r -> r.getType() == PermissionRestrictionType.NOVA_TERMINATION_VOTE
              && Boolean.TRUE.equals(r.getValue()));

      if (permission.getApplication() == ApplicationType.SEPODI) {
        return !(hasInfoPlus && hasNova);
      } else {
        return !(hasInfoPlus || hasNova);
      }
    });
  }

  @Schema(hidden = true)
  @JsonIgnore
  @AssertTrue(message = "Restrictions must be empty unless WRITER on non-BODI, SUPER_USER on SEPODI, or READER with only "
      + "NOVA/INFO_PLUS")
  public boolean isSboidsEmptyWhenNotWriterOrSuperUserOrBodi() {
    return permissions.stream().allMatch(permission -> {
      ApplicationRole role = permission.getRole();
      ApplicationType application = permission.getApplication();

      if (role == ApplicationRole.WRITER && application != ApplicationType.BODI) {
        return true;
      }

      if (role == ApplicationRole.SUPER_USER && application == ApplicationType.SEPODI) {
        return true;
      }

      if (role == ApplicationRole.READER) {
        return permission.getPermissionRestrictions().stream()
            .allMatch(r ->
                r.getType() == PermissionRestrictionType.INFO_PLUS_TERMINATION_VOTE
                    || r.getType() == PermissionRestrictionType.NOVA_TERMINATION_VOTE
            );
      }

      return permission.getPermissionRestrictions().isEmpty();
    });
  }

}
