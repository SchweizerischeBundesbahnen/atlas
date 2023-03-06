package ch.sbb.atlas.kafka.model.user.admin;

import ch.sbb.atlas.kafka.model.SwissCanton;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Gets serialized to kafka with class and package-name. Do care.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAdministrationPermissionModel implements Serializable {

  @Serial
  private static final long serialVersionUID = 1;

  private ApplicationRole role;

  private ApplicationType application;

  @Builder.Default
  private Set<String> sboids = new HashSet<>();

  @Builder.Default
  private Set<SwissCanton> swissCantons = new HashSet<>();

}