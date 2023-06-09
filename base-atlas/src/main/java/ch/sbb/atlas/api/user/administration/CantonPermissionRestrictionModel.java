package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.SwissCanton;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class CantonPermissionRestrictionModel extends PermissionRestrictionModel<SwissCanton> {

  public CantonPermissionRestrictionModel() {
    super(PermissionRestrictionType.CANTON);
  }

  public CantonPermissionRestrictionModel(SwissCanton value) {
    super(PermissionRestrictionType.CANTON);
    this.value = value;
  }

  @NotNull
  private SwissCanton value;

  @Override
  public String getValueAsString() {
    return getValue().name();
  }

  @Override
  public void setValueAsString(String value) {
    setValue(SwissCanton.valueOf(value));
  }
}
