package ch.sbb.atlas.kafka.model.user.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAdministrationModel {

  private String sbbUserId;

  @Builder.Default
  private Set<UserAdministrationPermissionModel> permissions = new HashSet<>();

}
