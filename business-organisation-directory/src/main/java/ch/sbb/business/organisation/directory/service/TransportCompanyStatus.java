package ch.sbb.business.organisation.directory.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(enumAsRef = true)
public enum TransportCompanyStatus {

  OPERATOR, CURRENT, SUPERVISION, OPERATING_PART, LIQUIDATED, INACTIVE

}
