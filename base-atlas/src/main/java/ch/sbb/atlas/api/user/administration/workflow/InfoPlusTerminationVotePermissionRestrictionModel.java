package ch.sbb.atlas.api.user.administration.workflow;

import ch.sbb.atlas.api.user.administration.PermissionRestrictionModel;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class InfoPlusTerminationVotePermissionRestrictionModel extends PermissionRestrictionModel<Boolean> {

  public InfoPlusTerminationVotePermissionRestrictionModel() {
    super(PermissionRestrictionType.INFO_PLUS_TERMINATION_VOTE);
  }

  public InfoPlusTerminationVotePermissionRestrictionModel(Boolean value) {
    super(PermissionRestrictionType.INFO_PLUS_TERMINATION_VOTE);
    this.value = value;
  }

  @NotNull
  private Boolean value;

  @Override
  public String getValueAsString() {
    return String.valueOf(getValue());
  }

  @Override
  public void setValueAsString(String value) {
    setValue(Boolean.valueOf(value));
  }
}
