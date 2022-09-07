package ch.sbb.atlas.user.administration.security.model;

import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@Data
public class UserPermissionModel {

  private ApplicationRole role;

  private ApplicationType application;

  @Builder.Default
  private Set<String> sboids = new HashSet<>();

}