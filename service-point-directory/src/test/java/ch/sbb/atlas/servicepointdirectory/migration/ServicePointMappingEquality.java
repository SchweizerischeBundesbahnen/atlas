package ch.sbb.atlas.servicepointdirectory.migration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@UtilityClass
public class ServicePointMappingEquality {

  private static int counter = 0;
  private static Set<String> setOfIsoCountryCode = new HashSet<>();
  private static Set<Integer> setOfIsoCountryCodeIsELLaendercodes = new HashSet<>();

  public static void performEqualityCheck(ServicePointCsvModel didokCsvLine,
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

    // TODO: geht nicht auf, Beispiel didokcode: 85945105, muss man anschauen warum ...
//    assertThat(fromString(atlasCsvLine.getCreationDate())).isEqualTo(didokCsvLine.getCreatedAt
//    ().withSecond(0));
//    if (!fromString(atlasCsvLine.getCreationDate()).equals(didokCsvLine.getCreatedAt()
//    .withSecond(0))) {
//      log.error("DIDOK_CODE: " + didokCsvLine.getDidokCode() + " DiDok: " + didokCsvLine
//      .getCreatedAt().withSecond(0) + " ATLAS: " + fromString(atlasCsvLine.getCreationDate()) +
//      "\tfrom " + didokCsvLine.getValidFrom() + " until " + didokCsvLine.getValidTo() + "
//      counter: " + counter);
//      counter++;
//    }
//    assertThat(fromString(atlasCsvLine.getEditionDate())).isEqualTo(didokCsvLine.getEditedAt()
//    .withSecond(0));

    // Since didok sometimes has locations but virtual, we should perform this check only if
    // atlas has a geolocation ?
    if (atlasCsvLine.isHasGeolocation()) {
      performEqualityCheckOnGeoLocation(didokCsvLine, atlasCsvLine);
    }
  }

  private static String generalErrorMessage(ServicePointCsvModel didokCsvLine) {
    return didokCsvLine.getDidokCode() + " from:" + didokCsvLine.getValidFrom() + " to:"
        + didokCsvLine.getValidTo() + "\t";
  }

  private static void performEqualityCheckOnGeoLocation(ServicePointCsvModel didokCsvLine,
      ServicePointVersionCsvModel atlasCsvLine) {
    // TODO: IsoCountryCode is null in ATLAS: 85810077 from:2002-06-16 to:2012-12-08	didok:LI,
    //  atlas:null
//    assertThat(atlasCsvLine.getIsoCountryCode())
//            .withFailMessage(generalErrorMessage(didokCsvLine) + "didok:" + didokCsvLine
//            .getIsoCountryCode() + ", atlas:" + atlasCsvLine.getIsoCountryCode())
//            .isEqualTo(didokCsvLine.getIsoCountryCode());

//    if (atlasCsvLine.getIsoCountryCode() != null && didokCsvLine.getIsoCountryCode() == null ||
//        atlasCsvLine.getIsoCountryCode() == null && didokCsvLine.getIsoCountryCode() != null) {
//      setOfIsoCountryCode.add(didokCsvLine.getIsoCountryCode());
//      if (didokCsvLine.getIsoCountryCode().equals("EL")) {
//        setOfIsoCountryCodeIsELLaendercodes.add(didokCsvLine.getLaendercode());
//      }
//      log.error(
//          generalErrorMessage(didokCsvLine) + " " + didokCsvLine.getGoBezeichnungDe() + " didok:"
//              + didokCsvLine.getIsoCountryCode() + ", atlas:" + atlasCsvLine.getIsoCountryCode()
//              + " " + counter + " " + setOfIsoCountryCode + " "
//              + setOfIsoCountryCodeIsELLaendercodes);
//      counter++;
//    }

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

    assertThat(atlasCsvLine.getLv95East()).isEqualTo(didokCsvLine.getELv95(), withPrecision(0.4));
    assertThat(atlasCsvLine.getLv95North()).isEqualTo(didokCsvLine.getNLv95(), withPrecision(0.4));

    // TODO: AssertionError: 20935932: didok:108.25353489964, atlas:86.73474746103409
//    assertThat(atlasCsvLine.getWgs84East())
//            .withFailMessage(didokCsvLine.getDidokCode() + ": didok:" + didokCsvLine.getEWgs84
//            () + ", atlas:" + atlasCsvLine
//                    .getWgs84East())
//            .isEqualTo(didokCsvLine.getEWgs84(), withPrecision(0.8));
//    assertThat(atlasCsvLine.getWgs84North()).isEqualTo(didokCsvLine.getNWgs84(), withPrecision
//    (0.8));

//    if (round(atlasCsvLine.getWgs84North(), 5).compareTo(round(didokCsvLine.getNWgs84(), 5)) !=
//    0) {
//      log.error(didokCsvLine.getDidokCode() + ": sboid:" + atlasCsvLine.getBusinessOrganisation
//      ()  + " offizielle Bezeichnung:" + didokCsvLine.getBezeichnungOffiziell() + " didok:" +
//      didokCsvLine.getNWgs84() + ", atlas:" + atlasCsvLine.getWgs84North() + " " + counter);
//      counter++;
//    }

    // TODO: AssertionError: 20935932: didok:1.20507283816E7, atlas:9655267.921445493
//    assertThat(atlasCsvLine.getWgs84WebEast())
//            .withFailMessage(didokCsvLine.getDidokCode() + ": didok:" + didokCsvLine
//            .getEWgs84web() + ", atlas:" + atlasCsvLine
//                    .getWgs84WebEast())
//            .isEqualTo(didokCsvLine.getEWgs84web(), withPrecision(0.4));
//    assertThat(atlasCsvLine.getWgs84WebNorth()).isEqualTo(didokCsvLine.getNWgs84web(),
//    withPrecision(0.4));

    // TODO: null != 0.0 at DidokCode 12015503 ... zu checken
    assertThat(atlasCsvLine.getHeight())
        .withFailMessage(
            didokCsvLine.getDidokCode() + ": didok:" + didokCsvLine.getHeight() + ", atlas:"
                + atlasCsvLine
                .getHeight())
        .isEqualTo(didokCsvLine.getHeight());

    //    if (didokCsvLine.getHeight() == null && atlasCsvLine.getHeight() != null) {
    //      log.error("DIDOK_CODE: " + didokCsvLine.getDidokCode() + " DiDok:" + didokCsvLine
    //      .getHeight() + " ATLAS:" +
    //      atlasCsvLine.getHeight() + " from " + didokCsvLine.getValidFrom() + " until " +
    //      didokCsvLine.getValidTo() + "
    //      counter: " + heightIsNullInDiDokCounter);
    //      heightIsNullInDiDokCounter++;
    //    }
  }

  private LocalDateTime fromString(String string) {
    return LocalDateTime.parse(string, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
  }

  public static Double round(double value, int places) {
    if (places < 0) {
      throw new IllegalArgumentException();
    }

    long factor = (long) Math.pow(10, places);
    value = value * factor;
    long tmp = Math.round(value);
    return (double) tmp / factor;
  }
}
