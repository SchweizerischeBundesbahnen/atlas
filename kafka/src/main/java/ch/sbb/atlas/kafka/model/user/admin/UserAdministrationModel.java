package ch.sbb.atlas.kafka.model.user.admin;

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
public class UserAdministrationModel implements Serializable {

  @Serial
  private static final long serialVersionUID = 1;

  private String sbbUserId;

  @Builder.Default
  private Set<UserAdministrationPermissionModel> permissions = new HashSet<>();

}
