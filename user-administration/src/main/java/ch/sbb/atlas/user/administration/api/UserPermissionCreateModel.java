package ch.sbb.atlas.user.administration.api;

import ch.sbb.atlas.user.administration.models.UserPermissionModel;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class UserPermissionCreateModel {

  @NotNull
  @Size(min = 7, max = 7)
  private String sbbUserId;

  @NotNull
  @Size(min = 1)
  private List<@Valid @NotNull UserPermissionModel> permissions;

}
