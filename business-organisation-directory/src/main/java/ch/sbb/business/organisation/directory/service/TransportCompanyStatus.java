package ch.sbb.business.organisation.directory.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(enumAsRef = true)
public enum TransportCompanyStatus {

  // Betreiber
  @JsonProperty("1")
  OPERATOR,
  // Aktuell
  @JsonProperty("2")
  CURRENT,
  // Aufsicht
  @JsonProperty("3")
  SUPERVISION,
  // Betriebsteil
  @JsonProperty("4")
  OPERATING_PART,
  // Liquidiert
  @JsonProperty("5")
  LIQUIDATED,
  // Inaktiv
  @JsonProperty("6")
  INACTIVE

}
