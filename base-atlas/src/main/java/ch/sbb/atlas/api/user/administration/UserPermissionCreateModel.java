package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(name = "UserPermissionCreate")
public class UserPermissionCreateModel {

  @Schema(description = "SBB User-ID", example = "u123456")
  @NotEmpty
  private String sbbUserId;

  @Schema(description = "User permissions")
  @NotNull
  @Size(min = 1)
  private List<@Valid @NotNull UserPermissionModel> permissions;

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
  @AssertTrue(message = "Sboids must be empty when not WRITER role or BODI ApplicationType")
  boolean isSboidsEmptyWhenNotWriterOrBodi() {
    return permissions.stream().noneMatch(
        permission ->
            (permission.getRole() != ApplicationRole.WRITER
                || permission.getApplication() == ApplicationType.BODI)
                && permission.getSboids().size() > 0
    );
  }

}
