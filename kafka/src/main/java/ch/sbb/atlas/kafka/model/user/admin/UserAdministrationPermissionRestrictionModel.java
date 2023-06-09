package ch.sbb.atlas.kafka.model.user.admin;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Gets serialized to kafka with class and package-name. Do care.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAdministrationPermissionRestrictionModel implements Serializable {

  @Serial
  private static final long serialVersionUID = 1;

  private String value;

  private PermissionRestrictionType restrictionType;

}