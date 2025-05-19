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
  @AssertTrue(message = "Restrictions must be empty unless role is WRITER on a non-BODI application, SUPERUSER on SEPODI, or "
      + "READER with at most one of INFO_PLUS or NOVA restriction")
  public boolean isSboidsEmptyWhenNotWriterOrSuperUserOrBodi() {
    for (PermissionModel permission : permissions) {
      if (!isPermissionValid(permission)) {
        return false;
      }
    }
    return true;
  }

  private boolean isPermissionValid(PermissionModel permission) {
    ApplicationRole role = permission.getRole();
    ApplicationType application = permission.getApplication();

    boolean hasRestrictions = !permission.getPermissionRestrictions().isEmpty();
    boolean hasAtMostOneTerminationVoteRestriction = hasAtMostOneTerminationVoteRestriction(permission);

    boolean writerIsValid = role == ApplicationRole.WRITER
        && application != ApplicationType.BODI
        && hasAtMostOneTerminationVoteRestriction;

    boolean superUserIsValid = role == ApplicationRole.SUPER_USER
        && application == ApplicationType.SEPODI
        && hasAtMostOneTerminationVoteRestriction;

    boolean readerIsValid = role == ApplicationRole.READER
        && hasAtMostOneTerminationVoteRestriction;

    return writerIsValid || superUserIsValid || readerIsValid || !hasRestrictions;
  }

  private boolean hasAtMostOneTerminationVoteRestriction(PermissionModel permission) {
    boolean hasInfoPlus = false;
    boolean hasNova = false;

    for (PermissionRestrictionModel<?> restriction : permission.getPermissionRestrictions()) {
      if (!Boolean.TRUE.equals(restriction.getValue())) {
        continue;
      }
      if (restriction.getType() == PermissionRestrictionType.INFO_PLUS_TERMINATION_VOTE) {
        hasInfoPlus = true;
      }
      if (restriction.getType() == PermissionRestrictionType.NOVA_TERMINATION_VOTE) {
        hasNova = true;
      }
    }

    return !(hasInfoPlus && hasNova);
  }

}
