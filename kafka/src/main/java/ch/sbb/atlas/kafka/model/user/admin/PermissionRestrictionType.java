package ch.sbb.atlas.kafka.model.user.admin;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum PermissionRestrictionType {

  BUSINESS_ORGANISATION,
  CANTON,
  COUNTRY,
  BULK_IMPORT,
  INFO_PLUS_TERMINATION_VOTE,
  NOVA_TERMINATION_VOTE
}
