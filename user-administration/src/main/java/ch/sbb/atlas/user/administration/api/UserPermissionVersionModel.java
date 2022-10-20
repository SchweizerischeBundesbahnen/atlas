package ch.sbb.atlas.user.administration.api;

import ch.sbb.atlas.base.service.model.api.BaseVersionModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class UserPermissionVersionModel extends BaseVersionModel {

  @NotNull
  private ApplicationRole role;

  @NotNull
  private ApplicationType application;

  @Schema(description = "Business Organisation - sboids", type = "List", example = "[\"ch:1:sboid:100000\"]")
  @NotNull
  @Builder.Default
  private List<@NotEmpty String> sboids = new ArrayList<>();

  public static UserPermissionVersionModel toModel(UserPermission userPermission) {
    return UserPermissionVersionModel.builder()
        .role(userPermission.getRole())
        .application(userPermission.getApplication())
        .sboids(userPermission.getSboid().stream().toList())
        .editor(userPermission.getEditor())
        .editionDate(userPermission.getEditionDate())
        .creator(userPermission.getCreator())
        .creationDate(userPermission.getCreationDate())
        .build();
  }

}
