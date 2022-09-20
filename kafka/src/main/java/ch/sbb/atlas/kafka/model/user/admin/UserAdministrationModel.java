package ch.sbb.atlas.kafka.model.user.admin;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAdministrationModel {

  private String sbbUserId;

  @Builder.Default
  private Set<UserAdministrationPermissionModel> permissions = new HashSet<>();

}
