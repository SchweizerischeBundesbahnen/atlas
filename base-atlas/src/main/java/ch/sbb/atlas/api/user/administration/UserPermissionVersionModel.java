package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.base.service.model.api.BaseVersionModel;
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
public class UserPermissionVersionModel extends BaseVersionModel {

  @NotNull
  private ApplicationRole role;

  @NotNull
  private ApplicationType application;

  @Schema(description = "Business Organisation - sboids", type = "List", example = "[\"ch:1:sboid:100000\"]")
  @NotNull
  @Builder.Default
  private List<@NotEmpty String> sboids = new ArrayList<>();

}
