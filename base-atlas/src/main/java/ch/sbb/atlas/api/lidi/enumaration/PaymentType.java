package ch.sbb.atlas.api.lidi.enumaration;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @deprecated since V2.544.0
 */
@Schema(enumAsRef = true)
@Deprecated(forRemoval = true, since = "2.328.0")
public enum PaymentType {
  INTERNATIONAL, REGIONAL, REGIONALWITHOUT, LOCAL, OTHER, NONE
}
