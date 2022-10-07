package ch.sbb.atlas.base.service.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * See https://confluence.sbb.ch/display/ATLAS/Status
 */
@Schema(enumAsRef = true)
public enum Status {

  DRAFT,
  VALIDATED,
  IN_REVIEW,
  WITHDRAWN,
  REVOKED

}
