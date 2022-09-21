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
public class UserAdministrationPermissionModel {

  private ApplicationRole role;

  private ApplicationType application;

  @Builder.Default
  private Set<String> sboids = new HashSet<>();

}