package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.api.user.administration.enumeration.PermissionRestrictionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class SboidPermissionRestrictionModel extends PermissionRestrictionModel<String> {

  public SboidPermissionRestrictionModel() {
    super(PermissionRestrictionType.BUSINESS_ORGANISATION);
  }

  public SboidPermissionRestrictionModel(String value) {
    super(PermissionRestrictionType.BUSINESS_ORGANISATION);
    this.value = value;
  }

  @Schema(example = "ch:1:sboid:123123")
  @NotNull
  private String value;

  @Override
  public String getValueAsString() {
    return getValue();
  }

  @Override
  public void setValueAsString(String value) {
    setValue(value);
  }
}
