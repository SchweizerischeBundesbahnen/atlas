package ch.sbb.atlas.servicepointdirectory.migration;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Data
public class ServicePointMappingEquality {

  private static final String SBOID_FIKTIVE_GO_INFOPLUS = "ch:1:sboid:101257";

  private final ServicePointCsvModel didokCsvLine;
  private final ServicePointVersionCsvModel atlasCsvLine;

  public void performCheck() {
    performCoreDataCheck();
    performTypeChecks();
    performBusinessOrganisationCheck();
    performMeansOfTransportCheck();
    performCategoryCheck();

    // TODO: check after https://flow.sbb.ch/browse/ATLAS-1318 and https://flow.sbb
    //  .ch/browse/ATLAS-873
    //assertThat(atlasCsvLine.fotComment).isEqualTo(didokCsvLine.BAV_BEMERKUNG);

    performCreatedAndEditedCheck();

    // Since didok sometimes has locations but virtual, we should perform this check only if atlas has a geolocation ?
    if (atlasCsvLine.isHasGeolocation()) {
      performEqualityCheckOnGeoLocation();
    }
  }

  private void performCoreDataCheck() {
    assertThat(atlasCsvLine.getNumber()).isEqualTo(didokCsvLine.getDidokCode());
    assertThat(atlasCsvLine.getNumberShort()).isEqualTo(didokCsvLine.getNummer());
    assertThat(atlasCsvLine.getUicCountryCode()).isEqualTo(didokCsvLine.getLaendercode());

    // TODO: actual_date: why does DIDOK don't export the SLOID?
//    assertThat(atlasCsvLine.getSloid())
//        .withFailMessage(
//            generalErrorMessage(didokCsvLine) + "didok:" + didokCsvLine.getSloid() + ", atlas:"
//                + atlasCsvLine.getSloid())
//        .isEqualTo(didokCsvLine.getSloid());

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

  private void performCreatedAndEditedCheck() {
    if (AtlasCsvReader.dateFromString(atlasCsvLine.getValidFrom()).equals(didokCsvLine.getValidFrom())) {
      assertThat(AtlasCsvReader.timestampFromString(atlasCsvLine.getCreationDate()))
          .isEqualToIgnoringNanos(didokCsvLine.getCreatedAt());
      assertThat(AtlasCsvReader.timestampFromString(atlasCsvLine.getEditionDate()))
          .isEqualToIgnoringNanos(didokCsvLine.getEditedAt());
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
    assertThat(atlasCsvLine.getHeight()).isEqualTo(didokCsvLine.getHeight());
  }

  private void performEqualityCheckOnCoordinates() {
    assertThat(atlasCsvLine.getLv95East()).isEqualTo(didokCsvLine.getELv95(), DoubleAssertion.equalOnDecimalDigits(2));
    assertThat(atlasCsvLine.getLv95North()).isEqualTo(didokCsvLine.getNLv95(), DoubleAssertion.equalOnDecimalDigits(2));

    performEqualityCheckOrIgnoreInfoplus(atlasCsvLine, atlasCsvLine.getWgs84East(), didokCsvLine.getEWgs84(), 7);
    performEqualityCheckOrIgnoreInfoplus(atlasCsvLine, atlasCsvLine.getWgs84North(), didokCsvLine.getNWgs84(), 7);

    // TODO: Change from 1076444.88305452 to 1076444.88205452 on 0.001 in
    //  DIDOK3_DIENSTSTELLEN_ALL_V_3_20230712021552.csv was not recognized,
    //  DIDOK_CODE=11023754
//    performEqualityCheckOrIgnoreInfoplus(atlasCsvLine, atlasCsvLine.getWgs84WebEast(),
//        didokCsvLine.getEWgs84web(), 3);
//    if (Double.valueOf(1076444.88).equals(didokCsvLine.getEWgs84web())) {
//      log.error(
//          generalErrorMessage(didokCsvLine) + " didok: " + didokCsvLine.getEWgs84web() + " atlas: "
//              + atlasCsvLine.getWgs84East());
//    }
//    performEqualityCheckOrIgnoreInfoplus(atlasCsvLine, atlasCsvLine.getWgs84WebNorth(),
//        didokCsvLine.getNWgs84web(), 3);
  }

  private static void performEqualityCheckOrIgnoreInfoplus(ServicePointVersionCsvModel atlasCsvLine,
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

  private String generalErrorMessage(ServicePointCsvModel didokCsvLine) {
    return didokCsvLine.getDidokCode() + " from:" + didokCsvLine.getValidFrom() + " to:"
        + didokCsvLine.getValidTo() + "\t";
  }
}
