package ch.sbb.atlas.api.user.administration.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum BeneficiaryType {

  USER,
  CLIENT_CREDENTIAL
}
