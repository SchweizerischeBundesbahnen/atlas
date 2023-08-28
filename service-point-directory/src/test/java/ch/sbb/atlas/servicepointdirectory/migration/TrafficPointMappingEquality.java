package ch.sbb.atlas.servicepointdirectory.migration;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointElementCsvModel;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class TrafficPointMappingEquality {

  private final TrafficPointElementCsvModel didokCsvLine;
  private final TrafficPointVersionCsvModel atlasCsvLine;

  public void performCheck() {
    performCoreDataCheck();

  }

  private void performCoreDataCheck() {
    assertThat(atlasCsvLine.getSloid()).isEqualTo(didokCsvLine.getSloid());
    assertThat(atlasCsvLine.getNumberShort()).isEqualTo(
        ServicePointNumber.of(didokCsvLine.getServicePointNumber()).getNumberShort());
    assertThat(atlasCsvLine.getUicCountryCode()).isEqualTo(Country.from(didokCsvLine.getCountry()).getUicCode());
    assertThat(atlasCsvLine.getNumber()).isEqualTo(ServicePointNumber.of(didokCsvLine.getServicePointNumber()).getNumber());
    assertThat(atlasCsvLine.getDesignation()).isEqualTo(didokCsvLine.getDesignation());
    assertThat(atlasCsvLine.getDesignationOperational()).isEqualTo(didokCsvLine.getDesignationOperational());
    assertThat(atlasCsvLine.getLength()).isEqualTo(didokCsvLine.getLength());
    assertThat(atlasCsvLine.getBoardingAreaHeight()).isEqualTo(didokCsvLine.getBoardingAreaHeight());
    assertThat(atlasCsvLine.getCompassDirection()).isEqualTo(didokCsvLine.getCompassDirection());
    assertThat(atlasCsvLine.getParentSloid()).isEqualTo(didokCsvLine.getParentSloid());
    assertThat(atlasCsvLine.getTrafficPointElementType()).isEqualTo(
        TrafficPointElementType.fromValue(didokCsvLine.getTrafficPointElementType()).toString());
    checkGeolocationIntegrity(atlasCsvLine.getLv95East(), didokCsvLine.getELv95(), -2);
    checkGeolocationIntegrity(atlasCsvLine.getLv95North(), didokCsvLine.getNLv95(), -2);
    checkGeolocationIntegrity(atlasCsvLine.getWgs84East(), didokCsvLine.getEWgs84(), 2);
    checkGeolocationIntegrity(atlasCsvLine.getWgs84North(), didokCsvLine.getNWgs84(), 2);
    checkGeolocationIntegrity(atlasCsvLine.getWgs84WebEast(), didokCsvLine.getEWgs84web(), -2);
    checkGeolocationIntegrity(atlasCsvLine.getWgs84WebNorth(), didokCsvLine.getNWgs84web(), -2);

    // It makes no sense to check creationDate and editionDate as ATLAS potentially merges some versions
    // and hence these dates/times are not comparable
    //assertThat(atlasCsvLine.getCreationDate()).isEqualTo(didokCsvLine.getCreatedAt());
    //assertThat(atlasCsvLine.getEditionDate()).isEqualTo(didokCsvLine.getEditedAt());

    if (atlasCsvLine.getParentSloidServicePoint() != null) {
      assertThat(getParentServicePointNumberFromParentSLOID()).isEqualTo(
          ServicePointNumber.of(didokCsvLine.getServicePointNumber()).getNumberShort());
    }

    assertThat(atlasCsvLine.getDesignationOfficial()).isEqualTo(didokCsvLine.getDesignationOfficial());
    assertThat(getServicePointBusinessOrgnatisationSaidFromSboid()).isEqualTo(didokCsvLine.getServicePointBusinessOrganisation());
    // TODO: Comment back in when https://flow.sbb.ch/browse/ATLAS-1395 is done
    //    assertThat(atlasCsvLine.getServicePointBusinessOrganisationNumber()).isEqualTo(didokCsvLine
    //    .getServicePointBusinessOrganisationNumber());

    // The abbreviations are not available in the DiDok-Exports, that is why they are commented out
    //    assertThat(atlasCsvLine.getServicePointBusinessOrganisationAbbreviationDe()).isEqualTo(
    //        didokCsvLine.getServicePointBusinessOrganisationAbbreviationDe());
    //    assertThat(atlasCsvLine.getServicePointBusinessOrganisationAbbreviationFr()).isEqualTo(
    //        didokCsvLine.getServicePointBusinessOrganisationAbbreviationFr());
    //    assertThat(atlasCsvLine.getServicePointBusinessOrganisationAbbreviationIt()).isEqualTo(
    //        didokCsvLine.getServicePointBusinessOrganisationAbbreviationIt());
    //    assertThat(atlasCsvLine.getServicePointBusinessOrganisationAbbreviationEn()).isEqualTo(
    //        didokCsvLine.getServicePointBusinessOrganisationAbbreviationEn());

  }

  private Integer getParentServicePointNumberFromParentSLOID() {
    return Integer.valueOf(
        atlasCsvLine.getParentSloidServicePoint().substring(atlasCsvLine.getParentSloidServicePoint().lastIndexOf(":") + 1));
  }

  private Integer getServicePointBusinessOrgnatisationSaidFromSboid() {
    return Integer.valueOf(
        atlasCsvLine.getServicePointBusinessOrganisation()
            .substring(atlasCsvLine.getServicePointBusinessOrganisation().lastIndexOf(":") + 1));
  }

  private void checkGeolocationIntegrity(Double atlasGeo, Double didokGeo, int digitChecks) {
    if (atlasGeo != null && didokGeo != null) {
      assertThat(atlasGeo).isEqualTo(didokGeo, DoubleAssertion.equalOnDecimalDigits(digitChecks));
    }
  }

}
