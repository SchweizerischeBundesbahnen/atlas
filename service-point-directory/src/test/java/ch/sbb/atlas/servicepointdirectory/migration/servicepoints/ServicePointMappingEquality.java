package ch.sbb.atlas.servicepointdirectory.migration.servicepoints;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.DoubleAssertion;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @param isFullExport ActualDate and FutureTimetable does not export SLOID in Didok
 */
@Slf4j
public record ServicePointMappingEquality(ServicePointDidokCsvModel didokCsvLine,
                                          ServicePointAtlasCsvModel atlasCsvLine,
                                          boolean isFullExport) {

  private static final String SBOID_FIKTIVE_GO_INFOPLUS = "ch:1:sboid:101257";

  public void performCheck() {
    performCoreDataCheck();
    performTypeChecks();
    performBusinessOrganisationCheck();
    performMeansOfTransportCheck();
    performCategoryCheck();

    assertThat(atlasCsvLine.getFotComment()).isEqualTo(didokCsvLine.getComment());

    // Since didok sometimes has locations but virtual, we should perform this check only if atlas has a geolocation ?
    if (atlasCsvLine.isHasGeolocation()) {
      performEqualityCheckOnGeoLocation();
    }
  }

  private void performCoreDataCheck() {
    assertThat(atlasCsvLine.getNumber()).isEqualTo(didokCsvLine.getDidokCode());
    assertThat(atlasCsvLine.getNumberShort()).isEqualTo(didokCsvLine.getNummer());
    assertThat(atlasCsvLine.getUicCountryCode()).isEqualTo(didokCsvLine.getLaendercode());

    if (isFullExport) {
      assertThat(atlasCsvLine.getSloid()).isEqualTo(didokCsvLine.getSloid());
    }

    assertThat(atlasCsvLine.getDesignationOfficial()).isEqualTo(
        didokCsvLine.getBezeichnungOffiziell());
    assertThat(atlasCsvLine.getDesignationLong()).isEqualTo(didokCsvLine.getBezeichnungLang());

    assertThat(atlasCsvLine.getAbbreviation()).isEqualTo(didokCsvLine.getAbkuerzung());
  }

  private void performTypeChecks() {
    assertThat(atlasCsvLine.isOperatingPoint()).isEqualTo(didokCsvLine.getIsBetriebspunkt());
    assertThat(atlasCsvLine.isOperatingPointWithTimetable()).isEqualTo(
        didokCsvLine.getIsFahrplan());
    assertThat(atlasCsvLine.isStopPoint()).isEqualTo(didokCsvLine.getIsHaltestelle());

    if (atlasCsvLine.getStopPointType() != null) {
      assertThat(atlasCsvLine.getStopPointType().getId()).isEqualTo(didokCsvLine.getHTypId());
    } else {
      assertThat(didokCsvLine.getHTypId()).isNull();
    }

    assertThat(atlasCsvLine.isFreightServicePoint()).isEqualTo(didokCsvLine.getIsBedienpunkt());
    assertThat(atlasCsvLine.isTrafficPoint()).isEqualTo(didokCsvLine.getIsVerkehrspunkt());
    assertThat(atlasCsvLine.isBorderPoint()).isEqualTo(didokCsvLine.getIsGrenzpunkt());
    if (atlasCsvLine.getOperatingPointType() != null) {
      assertThat(atlasCsvLine.getOperatingPointType().getId()).isEqualTo(
          didokCsvLine.getBpBetriebspunktArtId());
    } else {
      assertThat(didokCsvLine.getBpBetriebspunktArtId()).isNull();
    }

    if (atlasCsvLine.getOperatingPointTechnicalTimetableType() != null) {
      assertThat(atlasCsvLine.getOperatingPointTechnicalTimetableType().getId()).isEqualTo(
          didokCsvLine.getBptfBetriebspunktArtId());
    } else {
      assertThat(didokCsvLine.getBptfBetriebspunktArtId()).isNull();
    }

    if (atlasCsvLine.getOperatingPointTrafficPointType() != null) {
      assertThat(atlasCsvLine.getOperatingPointTrafficPointType().getId()).isEqualTo(
          didokCsvLine.getBpvbBetriebspunktArtId());
    } else {
      assertThat(didokCsvLine.getBpvbBetriebspunktArtId()).isNull();
    }

    boolean isBpsInDidok = Boolean.TRUE.equals(didokCsvLine.getOperatingPointRouteNetwork());
    assertThat(atlasCsvLine.isOperatingPointRouteNetwork()).isEqualTo(isBpsInDidok);
    boolean isBpkInDidok = Boolean.TRUE.equals(didokCsvLine.getOperatingPointKilometer());
    assertThat(atlasCsvLine.isOperatingPointKilometer()).isEqualTo(isBpkInDidok);
    assertThat(atlasCsvLine.getOperatingPointKilometerMasterNumber()).isEqualTo(
        didokCsvLine.getOperatingPointKilometerMaster());
    assertThat(atlasCsvLine.getSortCodeOfDestinationStation()).isEqualTo(
        didokCsvLine.getRichtpunktCode());
  }

  private void performBusinessOrganisationCheck() {
    assertThat(atlasCsvLine.getBusinessOrganisation()).isEqualTo(
        "ch:1:sboid:" + didokCsvLine.getSaid());
    String didokSboid = "ch:1:sboid:" + didokCsvLine.getSaid();
    assertThat(atlasCsvLine.getBusinessOrganisation())
        .withFailMessage(generalErrorMessage(didokCsvLine) + "didok:" + didokSboid + ", atlas:"
            + atlasCsvLine.getBusinessOrganisation())
        .isEqualTo(didokSboid);

    // Atlas gibt nicht mehr gültige GO-Daten nicht mehr aus ...
    if (atlasCsvLine.getBusinessOrganisationNumber() != null) {
      assertThat(atlasCsvLine.getBusinessOrganisationNumber())
          .withFailMessage(
              didokCsvLine.getDidokCode() + ": didok:" + didokCsvLine.getGoNummer() + ", atlas:"
                  + atlasCsvLine
                  .getBusinessOrganisationNumber())
          .isEqualTo(didokCsvLine.getGoNummer());
    }
  }

  private void performCategoryCheck() {
    if (didokCsvLine.getDsKategorienIds() != null) {
      Set<String> expectedKategorien =
          Stream.of(didokCsvLine.getDsKategorienIds().split("\\|"))
              .filter(StringUtils::isNotBlank)
              .collect(Collectors.toSet());
      Set<String> actualCategories =
          Stream.of(atlasCsvLine.getCategories().split("\\|"))
              .map(i -> Category.valueOf(i).getCode())
              .collect(Collectors.toSet());
      assertThat(actualCategories).isEqualTo(expectedKategorien);
    } else {
      assertThat(atlasCsvLine.getCategories()).isNull();
    }
  }

  private void performMeansOfTransportCheck() {
    if (didokCsvLine.getBpvhVerkehrsmittel() != null) {
      Set<String> expectedVerkehrsmittel =
          Stream.of(didokCsvLine.getBpvhVerkehrsmittel().split("~"))
              .filter(StringUtils::isNotBlank)
              .collect(Collectors.toSet());
      Set<String> actualMeansOfTransport =
          Stream.of(atlasCsvLine.getMeansOfTransport().split("\\|"))
              .map(i -> MeanOfTransport.valueOf(i).getCode())
              .collect(Collectors.toSet());
      assertThat(actualMeansOfTransport).isEqualTo(expectedVerkehrsmittel);
    } else {
      assertThat(atlasCsvLine.getMeansOfTransport()).isNull();
    }
  }

  private void performEqualityCheckOnGeoLocation() {
    if (didokCsvLine.getIsoCountryCode() != null) {
      assertThat(Country.fromIsoCode(atlasCsvLine.getIsoCountryCode())).isEqualTo(
          Country.fromIsoCode(didokCsvLine.getIsoCountryCode()));
    }

    // Atlas liefert den Kanontsnamen immer, in Didok fehlt er ab und zu
    if (didokCsvLine.getKantonsName() != null) {
      assertThat(atlasCsvLine.getCantonName()).isEqualTo(didokCsvLine.getKantonsName());
    }
    assertThat(atlasCsvLine.getCantonFsoNumber()).isEqualTo(didokCsvLine.getKantonsNum());
    assertThat(atlasCsvLine.getCantonAbbreviation()).isEqualTo(didokCsvLine.getKantonsKuerzel());
    assertThat(atlasCsvLine.getDistrictName()).isEqualTo(didokCsvLine.getBezirksName());
    assertThat(atlasCsvLine.getDistrictFsoNumber()).isEqualTo(didokCsvLine.getBezirksNum());
    assertThat(atlasCsvLine.getMunicipalityName()).isEqualTo(didokCsvLine.getGemeindeName());
    assertThat(atlasCsvLine.getFsoNumber()).isEqualTo(didokCsvLine.getBfsNummer());
    assertThat(atlasCsvLine.getLocalityName()).isEqualTo(didokCsvLine.getOrtschaftsName());

    performEqualityCheckOnCoordinates();
    if (isFullExport) {
      assertThat(atlasCsvLine.getHeight()).isEqualTo(didokCsvLine.getHeight());
    }
  }

  private void performEqualityCheckOnCoordinates() {
    assertThat(atlasCsvLine.getLv95East())
        .withFailMessage("didokcode: " + didokCsvLine.getDidokCode() + ":" + didokCsvLine.getGoBezeichnungDe() + ":"
            + didokCsvLine.getValidFrom() +
            " atlas:" + atlasCsvLine.getValidFrom())
        .isEqualTo(didokCsvLine.getELv95(), DoubleAssertion.equalOnDecimalDigits(2));
    assertThat(atlasCsvLine.getLv95North()).isEqualTo(didokCsvLine.getNLv95(), DoubleAssertion.equalOnDecimalDigits(2));

    performEqualityCheckOrIgnoreInfoplus(atlasCsvLine, atlasCsvLine.getWgs84East(), didokCsvLine.getEWgs84(), 7);
    performEqualityCheckOrIgnoreInfoplus(atlasCsvLine, atlasCsvLine.getWgs84North(), didokCsvLine.getNWgs84(), 7);
  }

  private static void performEqualityCheckOrIgnoreInfoplus(ServicePointAtlasCsvModel atlasCsvLine,
      Double atlasValue, Double didokValue, int digits) {
    if (isBigDifferenceBetween(atlasValue, didokValue)) {
      assertThat(atlasCsvLine.getBusinessOrganisation()).isEqualTo(SBOID_FIKTIVE_GO_INFOPLUS);
    } else {
      assertThat(atlasValue).isEqualTo(didokValue, DoubleAssertion.equalOnDecimalDigits(digits));
    }
  }

  private static boolean isBigDifferenceBetween(Double x, Double y) {
    BigDecimal difference = BigDecimal.valueOf(x).subtract(BigDecimal.valueOf(y)).abs();
    return difference.compareTo(BigDecimal.valueOf(0.001)) > 0;
  }

  private String generalErrorMessage(ServicePointDidokCsvModel didokCsvLine) {
    return didokCsvLine.getDidokCode() + " from:" + didokCsvLine.getValidFrom() + " to:"
        + didokCsvLine.getValidTo() + "\t";
  }

}
