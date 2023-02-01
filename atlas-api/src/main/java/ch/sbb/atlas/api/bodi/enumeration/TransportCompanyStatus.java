package ch.sbb.atlas.api.bodi.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(enumAsRef = true)
public enum TransportCompanyStatus {

  OPERATOR,
  CURRENT,
  SUPERVISION,
  OPERATING_PART,
  LIQUIDATED,
  INACTIVE,
  FORMER_OPERATING_PART

}
