package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.api.user.administration.enumeration.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.SwissCanton;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@RequiredArgsConstructor
@SuperBuilder
@Schema(name = "PermissionRestriction")
public abstract class PermissionRestrictionModel<T> {

  protected final PermissionRestrictionType type;

  @Schema(oneOf = {String.class, SwissCanton.class} )
  public abstract T getValue();

  @JsonIgnore
  public abstract String getValueAsString();

  public abstract void setValueAsString(String value);

}
