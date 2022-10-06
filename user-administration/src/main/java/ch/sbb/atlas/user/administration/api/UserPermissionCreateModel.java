package ch.sbb.atlas.user.administration.api;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPermissionCreateModel {

  @NotNull
  @Size(min = 7, max = 7)
  private String sbbUserId;

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

  public List<UserPermission> toEntityList() {
    return permissions.stream().map(permission -> UserPermission.builder()
                                                                .sbbUserId(sbbUserId.toLowerCase())
                                                                .application(
                                                                    permission.getApplication())
                                                                .role(permission.getRole())
                                                                .sboid(new HashSet<>(
                                                                    permission.getSboids()))
                                                                .build()
    ).toList();
  }

}
