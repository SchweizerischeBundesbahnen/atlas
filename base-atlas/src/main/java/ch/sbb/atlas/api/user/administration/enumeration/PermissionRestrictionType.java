package ch.sbb.atlas.api.user.administration.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(enumAsRef = true)
public enum PermissionRestrictionType {

  BUSINESS_ORGANISATION,
  CANTON,

  ;

}
