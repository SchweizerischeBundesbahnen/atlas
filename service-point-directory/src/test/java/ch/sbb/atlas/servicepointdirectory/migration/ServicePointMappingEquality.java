package ch.sbb.atlas.servicepointdirectory.migration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ServicePointMappingEquality {

  private static final String SBOID_FIKTIVE_GO_INFOPLUS = "ch:1:sboid:101257";

  private static int counter = 0;
  private static Set<String> setOfIsoCountryCode = new HashSet<>();
  private static Set<Integer> setOfIsoCountryCodeIsELLaendercodes = new HashSet<>();

  public void performEqualityCheck(ServicePointCsvModel didokCsvLine,
      ServicePointVersionCsvModel atlasCsvLine) {
    assertThat(atlasCsvLine.getNumber()).isEqualTo(didokCsvLine.getDidokCode());
    assertThat(atlasCsvLine.getNumberShort()).isEqualTo(didokCsvLine.getNummer());

    // TODO: actual_date: why does DIDOK don't export the SLOID?
    assertThat(atlasCsvLine.getSloid())
        .withFailMessage(
            generalErrorMessage(didokCsvLine) + "didok:" + didokCsvLine.getSloid() + ", atlas:"
                + atlasCsvLine.getSloid())
        .isEqualTo(didokCsvLine.getSloid());

    assertThat(atlasCsvLine.getDesignationOfficial()).isEqualTo(
        didokCsvLine.getBezeichnungOffiziell());
    assertThat(atlasCsvLine.getDesignationLong()).isEqualTo(didokCsvLine.getBezeichnungLang());

    assertThat(atlasCsvLine.getAbbreviation()).isEqualTo(didokCsvLine.getAbkuerzung());

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

    // TODO: check after https://flow.sbb.ch/browse/ATLAS-1318 and https://flow.sbb
    //  .ch/browse/ATLAS-873
    //assertThat(atlasCsvLine.fotComment).isEqualTo(didokCsvLine.BAV_BEMERKUNG);

    if (DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN_CH)
        .parse(atlasCsvLine.getValidFrom())
        .equals(didokCsvLine.getValidFrom())) {
      assertThat(fromString(atlasCsvLine.getCreationDate())).isEqualTo(
          didokCsvLine.getCreatedAt().withSecond(0));
      assertThat(fromString(atlasCsvLine.getEditionDate())).isEqualTo(
          didokCsvLine.getEditedAt().withSecond(0));
    }

    // Since didok sometimes has locations but virtual, we should perform this check only if atlas has a geolocation ?
    if (atlasCsvLine.isHasGeolocation()) {
      performEqualityCheckOnGeoLocation(didokCsvLine, atlasCsvLine);
    }
  }

  private String generalErrorMessage(ServicePointCsvModel didokCsvLine) {
    return didokCsvLine.getDidokCode() + " from:" + didokCsvLine.getValidFrom() + " to:"
        + didokCsvLine.getValidTo() + "\t";
  }

  private void performEqualityCheckOnGeoLocation(ServicePointCsvModel didokCsvLine,
      ServicePointVersionCsvModel atlasCsvLine) {
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

    performEqualityCheckOnCoordinates(didokCsvLine, atlasCsvLine);
    assertThat(atlasCsvLine.getHeight()).isEqualTo(didokCsvLine.getHeight());
  }

  private void performEqualityCheckOnCoordinates(ServicePointCsvModel didokCsvLine,
      ServicePointVersionCsvModel atlasCsvLine) {
    assertThat(atlasCsvLine.getLv95East()).isEqualTo(didokCsvLine.getELv95(), withPrecision(0.001));
    assertThat(atlasCsvLine.getLv95North()).isEqualTo(didokCsvLine.getNLv95(),
        withPrecision(0.001));

    performEqualityCheckOrIgnoreInfoplus(atlasCsvLine, atlasCsvLine.getWgs84East(),
        didokCsvLine.getEWgs84());
    performEqualityCheckOrIgnoreInfoplus(atlasCsvLine, atlasCsvLine.getWgs84North(),
        didokCsvLine.getNWgs84());

    // TODO: Change from 1076444.88305452 to 1076444.88205452 on 0.001 in
    //  DIDOK3_DIENSTSTELLEN_ALL_V_3_20230712021552.csv was not recognized,
    //  DIDOK_CODE=11023754
    performEqualityCheckOrIgnoreInfoplus(atlasCsvLine, atlasCsvLine.getWgs84WebEast(),
        didokCsvLine.getEWgs84web());
    if (Double.valueOf(1076444.88).equals(didokCsvLine.getEWgs84web())) {
      log.error(
          generalErrorMessage(didokCsvLine) + " didok: " + didokCsvLine.getEWgs84web() + " atlas: "
              + atlasCsvLine.getWgs84East());
    }
    performEqualityCheckOrIgnoreInfoplus(atlasCsvLine, atlasCsvLine.getWgs84WebNorth(),
        didokCsvLine.getNWgs84web());
  }

  private static void performEqualityCheckOrIgnoreInfoplus(ServicePointVersionCsvModel atlasCsvLine,
      Double atlasValue, Double didokValue) {
    if (isBigDifferenceBetween(atlasValue, didokValue)) {
      assertThat(atlasCsvLine.getBusinessOrganisation()).isEqualTo(SBOID_FIKTIVE_GO_INFOPLUS);
    } else {
      assertThat(atlasValue).isEqualTo(didokValue, withPrecision(0.001));
    }
  }

  private LocalDateTime fromString(String string) {
    return LocalDateTime.parse(string, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
  }

  private static boolean isBigDifferenceBetween(Double x, Double y) {
    BigDecimal difference = BigDecimal.valueOf(x).subtract(BigDecimal.valueOf(y)).abs();
    return difference.compareTo(BigDecimal.valueOf(0.001)) > 0;
  }
}
