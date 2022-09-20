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
public class UserAdministrationPermissionModel {

  private ApplicationRole role;

  private ApplicationType application;

  @Builder.Default
  private Set<String> sboids = new HashSet<>();

}