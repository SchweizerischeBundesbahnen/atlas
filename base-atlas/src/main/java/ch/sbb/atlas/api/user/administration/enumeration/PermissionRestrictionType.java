package ch.sbb.atlas.api.user.administration.enumeration;

import ch.sbb.atlas.kafka.model.SwissCanton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermissionRestrictionType {

  BUSINESS_ORGANISATION(String.class),
  CANTON(SwissCanton.class),

  ;

  private final Class<?> clazz;
}
