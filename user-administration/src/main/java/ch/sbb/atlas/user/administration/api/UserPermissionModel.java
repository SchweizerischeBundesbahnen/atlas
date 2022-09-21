package ch.sbb.atlas.user.administration.api;

import ch.sbb.atlas.user.administration.entity.UserPermission;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserPermissionModel {

  @NotNull
  private ApplicationRole role;

  @NotNull
  private ApplicationType application;

  @NotNull
  @Builder.Default
  private List<@NotEmpty String> sboids = new ArrayList<>();

  public static UserPermissionModel toModel(UserPermission userPermission) {
    return UserPermissionModel.builder()
                              .role(userPermission.getRole())
                              .application(userPermission.getApplication())
                              .sboids(userPermission.getSboid().stream().toList())
                              .build();
  }

}
