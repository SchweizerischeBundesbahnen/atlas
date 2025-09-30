package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.servicepoint.Country;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class CountryPermissionRestrictionModel extends PermissionRestrictionModel {

  public CountryPermissionRestrictionModel() {
    super(PermissionRestrictionType.COUNTRY);
  }

  @NotNull
  private Country value;

  @Override
  public String getValueAsString() {
    return getValue().name();
  }

  @Override
  public void setValueAsString(String value) {
    setValue(Country.valueOf(value));
  }
}
