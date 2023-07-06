package ch.sbb.atlas.servicepointdirectory.migration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ServicePointMappingEquality {

  public static void performEqualityCheck(ServicePointCsvModel didokCsvLine, ServicePointVersionCsvModel atlasCsvLine) {
    assertThat(atlasCsvLine.getNumber()).isEqualTo(didokCsvLine.getDidokCode());
    assertThat(atlasCsvLine.getNumberShort()).isEqualTo(didokCsvLine.getNummer());
    assertThat(atlasCsvLine.getSloid()).isEqualTo(didokCsvLine.getSloid());

    assertThat(atlasCsvLine.getDesignationOfficial()).isEqualTo(didokCsvLine.getBezeichnungOffiziell());
    assertThat(atlasCsvLine.getDesignationLong()).isEqualTo(didokCsvLine.getBezeichnungLang());

    assertThat(atlasCsvLine.getAbbreviation()).isEqualTo(didokCsvLine.getAbkuerzung());

    assertThat(atlasCsvLine.isOperatingPoint()).isEqualTo(didokCsvLine.getIsBetriebspunkt());
    assertThat(atlasCsvLine.isOperatingPointWithTimetable()).isEqualTo(didokCsvLine.getIsFahrplan());
    assertThat(atlasCsvLine.isStopPoint()).isEqualTo(didokCsvLine.getIsHaltestelle());

    if (atlasCsvLine.getStopPointTypeCode() != null) {
      assertThat(atlasCsvLine.getStopPointTypeCode().getId()).isEqualTo(didokCsvLine.getHTypId());
    } else {
      assertThat(didokCsvLine.getHTypId()).isNull();
    }

    assertThat(atlasCsvLine.isFreightServicePoint()).isEqualTo(didokCsvLine.getIsBedienpunkt());
    assertThat(atlasCsvLine.isTrafficPoint()).isEqualTo(didokCsvLine.getIsVerkehrspunkt());
    assertThat(atlasCsvLine.isBorderPoint()).isEqualTo(didokCsvLine.getIsGrenzpunkt());

    assertThat(atlasCsvLine.getSboid()).isEqualTo("ch:1:sboid:" + didokCsvLine.getSaid());
    /*
    assertThat(atlasCsvLine.getBusinessOrganisationOrganisationNumber()).isEqualTo(didokCsvLine.getGoNummer());

    assertThat(atlasCsvLine.getBusinessOrganisationAbbreviationDe()).isEqualTo(didokCsvLine.getGoAbkuerzungDe());
    assertThat(atlasCsvLine.getBusinessOrganisationAbbreviationFr()).isEqualTo(didokCsvLine.getGoAbkuerzungFr());
    assertThat(atlasCsvLine.getBusinessOrganisationAbbreviationIt()).isEqualTo(didokCsvLine.getGoAbkuerzungIt());
    assertThat(atlasCsvLine.getBusinessOrganisationAbbreviationEn()).isEqualTo(didokCsvLine.getGoAbkuerzungEn());

    assertThat(atlasCsvLine.getBusinessOrganisationDescriptionDe()).isEqualTo(didokCsvLine.getGoBezeichnungDe());
    assertThat(atlasCsvLine.getBusinessOrganisationDescriptionFr()).isEqualTo(didokCsvLine.getGoBezeichnungFr());
    assertThat(atlasCsvLine.getBusinessOrganisationDescriptionIt()).isEqualTo(didokCsvLine.getGoBezeichnungIt());
    assertThat(atlasCsvLine.getBusinessOrganisationDescriptionEn()).isEqualTo(didokCsvLine.getGoBezeichnungEn());
    */

    if (atlasCsvLine.getOperatingPointTypeCode() != null) {
      assertThat(atlasCsvLine.getOperatingPointTypeCode().getId()).isEqualTo(didokCsvLine.getBpBetriebspunktArtId());
    } else {
      assertThat(didokCsvLine.getBpBetriebspunktArtId()).isNull();
    }

    if (atlasCsvLine.getOperatingPointTechnicalTimetableTypeCode() != null) {
      assertThat(atlasCsvLine.getOperatingPointTechnicalTimetableTypeCode().getId()).isEqualTo(
          didokCsvLine.getBptfBetriebspunktArtId());
    } else {
      assertThat(didokCsvLine.getBptfBetriebspunktArtId()).isNull();
    }

    // TODO: Create mapping for DiDok to ATLAS, because e.g. DiDok=~Z~ and ATLAS=TRAIN
    //assertThat(atlasCsvLine.getMeansOfTransportCode()).isEqualTo(didokCsvLine.getBpvhVerkehrsmittel());

    // TODO: Mapping von ATLAS ("GSMR|MAINTENANCE_POINT|MIGRATION_MOBILE_EQUIPE") auf DiDok ("1|9|16") möglich machen
    // assertThat(atlasCsvLine.getCategoriesCode()).isEqualTo(didokCsvLine.getDsKategorienIds());

    if (atlasCsvLine.getOperatingPointTrafficPointTypeCode() != null) {
      assertThat(atlasCsvLine.getOperatingPointTrafficPointTypeCode().getId()).isEqualTo(
          didokCsvLine.getBpvbBetriebspunktArtId());
    } else {
      assertThat(didokCsvLine.getBpvbBetriebspunktArtId()).isNull();
    }

    boolean isBpsInDidok = Boolean.TRUE.equals(didokCsvLine.getOperatingPointRouteNetwork());
    assertThat(atlasCsvLine.isOperatingPointRouteNetwork()).isEqualTo(isBpsInDidok);
    boolean isBpkInDidok = Boolean.TRUE.equals(didokCsvLine.getOperatingPointKilometer());
    assertThat(atlasCsvLine.isOperatingPointKilometer()).isEqualTo(isBpkInDidok);
    assertThat(atlasCsvLine.getOperatingPointKilometerMasterNumber()).isEqualTo(didokCsvLine.getOperatingPointKilometerMaster());
    assertThat(atlasCsvLine.getSortCodeOfDestinationStation()).isEqualTo(didokCsvLine.getRichtpunktCode());

    // TODO: check after https://flow.sbb.ch/browse/ATLAS-1318 and https://flow.sbb.ch/browse/ATLAS-873
    //assertThat(atlasCsvLine.fotComment).isEqualTo(didokCsvLine.BAV_BEMERKUNG);

    // TODO: geht nicht auf, Beispiel didokcode: 85945105, muss man anschauen warum ...
    // assertThat(fromString(atlasCsvLine.getCreationDate())).isEqualTo(didokCsvLine.getCreatedAt().withSecond(0));
    // assertThat(fromString(atlasCsvLine.getEditionDate())).isEqualTo(didokCsvLine.getEditedAt().withSecond(0));

    // Since didok sometimes has locations but virtual, we should perform this check only if atlas has a geolocation ?
    if (atlasCsvLine.isHasGeolocation()) {
      performEqualityCheckOnGeoLocation(didokCsvLine, atlasCsvLine);
    }
  }

  private static void performEqualityCheckOnGeoLocation(ServicePointCsvModel didokCsvLine,
      ServicePointVersionCsvModel atlasCsvLine) {
    // AT != LI at DidokCode: 12018374 ... zu prüfen
    // assertThat(atlasCsvLine.getIsoCountryCode()).isEqualTo(didokCsvLine.getIsoCountryCode());

    // Zürich != null at DidokCode: 85306480
    // assertThat(atlasCsvLine.getCantonName()).isEqualTo(didokCsvLine.getKantonsName());
    assertThat(atlasCsvLine.getCantonFsoNumber()).isEqualTo(didokCsvLine.getKantonsNum());
    assertThat(atlasCsvLine.getCantonAbbreviation()).isEqualTo(didokCsvLine.getKantonsKuerzel());
    assertThat(atlasCsvLine.getDistrictName()).isEqualTo(didokCsvLine.getBezirksName());

    assertThat(atlasCsvLine.getDistrictFsoNumber()).isEqualTo(didokCsvLine.getBezirksNum());

    assertThat(atlasCsvLine.getMunicipalityName()).isEqualTo(didokCsvLine.getGemeindeName());

    assertThat(atlasCsvLine.getFsoNumber()).isEqualTo(didokCsvLine.getBfsNummer());

    assertThat(atlasCsvLine.getLocalityName()).isEqualTo(didokCsvLine.getOrtschaftsName());

    assertThat(atlasCsvLine.getLv95East()).isEqualTo(didokCsvLine.getELv95(), withPrecision(0.4));
    assertThat(atlasCsvLine.getLv95North()).isEqualTo(didokCsvLine.getNLv95(), withPrecision(0.4));

    /* TODO: AssertionError: 20935932: didok:108.25353489964, atlas:86.73474746103409
    assertThat(atlasCsvLine.getWgs84East())
        .withFailMessage(didokCsvLine.getDidokCode() + ": didok:" + didokCsvLine.getEWgs84() + ", atlas:" + atlasCsvLine.getWgs84East())
        .isEqualTo(didokCsvLine.getEWgs84(), withPrecision(0.8));
    assertThat(atlasCsvLine.getWgs84North()).isEqualTo(didokCsvLine.getNWgs84(), withPrecision(0.8));
    */

    /* TODO: AssertionError: 20935932: didok:1.20507283816E7, atlas:9655267.921445493
    assertThat(atlasCsvLine.getWgs84WebEast())
        .withFailMessage(didokCsvLine.getDidokCode() + ": didok:" + didokCsvLine.getEWgs84web() + ", atlas:" + atlasCsvLine.getWgs84WebEast())
        .isEqualTo(didokCsvLine.getEWgs84web(), withPrecision(0.4));
    assertThat(atlasCsvLine.getWgs84WebNorth()).isEqualTo(didokCsvLine.getNWgs84web(), withPrecision(0.4));
     */

    // TODO: null != 0.0 at DidokCode 12015503 ... zu checken
//    assertThat(atlasCsvLine.getHeight())
//        .withFailMessage(didokCsvLine.getDidokCode() + ": didok:" + didokCsvLine.getHeight() + ", atlas:" + atlasCsvLine.getHeight())
//        .isEqualTo(didokCsvLine.getHeight());
  }

  private LocalDateTime fromString(String string) {
    return LocalDateTime.parse(string, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
  }

}
