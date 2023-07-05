package ch.sbb.atlas.servicepointdirectory.migration;

import static org.assertj.core.api.Assertions.assertThat;

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
        // TODO: GO Nummer 1056 expected for DS: 56134502
        // assertThat(atlasCsvLine.getBusinessOrganisationOrganisationNumber()).isEqualTo(didokCsvLine.getGoNummer());

        // TODO: Warum werden die BO Infos nicht ins atlas csv gemappt?
    /*
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
            assertThat(atlasCsvLine.getOperatingPointTechnicalTimetableTypeCode().getId()).isEqualTo(didokCsvLine.getBptfBetriebspunktArtId());
        } else {
            assertThat(didokCsvLine.getBptfBetriebspunktArtId()).isNull();
        }

        // TODO: Create mapping for DiDok to ATLAS, because e.g. DiDok=~Z~ and ATLAS=TRAIN
        //assertThat(atlasCsvLine.getMeansOfTransportCode()).isEqualTo(didokCsvLine.getBpvhVerkehrsmittel());

        // TODO: Mapping von ATLAS ("GSMR|MAINTENANCE_POINT|MIGRATION_MOBILE_EQUIPE") auf DiDok ("1|9|16") möglich machen
        // assertThat(atlasCsvLine.getCategoriesCode()).isEqualTo(didokCsvLine.getDsKategorienIds());


        if (atlasCsvLine.getOperatingPointTrafficPointTypeCode() != null) {
            assertThat(atlasCsvLine.getOperatingPointTrafficPointTypeCode().getId()).isEqualTo(didokCsvLine.getBpvbBetriebspunktArtId());
        } else {
            assertThat(didokCsvLine.getBpvbBetriebspunktArtId()).isNull();
        }

        // TODO: For DIDOK_CODE=85173393 ATLAS=2x"FALSE" but DIDOK=1x"null" & 1x"0"
        // assertThat(atlasCsvLine.isOperatingPointRouteNetwork()).isEqualTo(didokCsvLine.getOperatingPointRouteNetwork());

        // TODO: For DIDOK_CODE=56134502 ATLAS=3x"TRUE", but DIDOK=3x"0"
        // assertThat(atlasCsvLine.isOperatingPointKilometer()).isEqualTo(didokCsvLine.getOperatingPointKilometer());

        // TODO: DiDok=null but ATLAS=0, e.g. für DIDOK_CODE=56134502
        // assertThat(atlasCsvLine.getOperatingPointKilometerMasterNumber()).isEqualTo(didokCsvLine.getOperatingPointKilometerMaster());

        assertThat(atlasCsvLine.getSortCodeOfDestinationStation()).isEqualTo(didokCsvLine.getRichtpunktCode());

        // TODO: check after https://flow.sbb.ch/browse/ATLAS-1318 and https://flow.sbb.ch/browse/ATLAS-873
        //assertThat(atlasCsvLine.fotComment).isEqualTo(didokCsvLine.BAV_BEMERKUNG);

        // TODO: geht nicht auf, Beispiel didokcode: 85945105, muss man anschauen warum ...
        // assertThat(fromString(atlasCsvLine.getCreationDate())).isEqualTo(didokCsvLine.getCreatedAt().withSecond(0));
        // assertThat(fromString(atlasCsvLine.getEditionDate())).isEqualTo(didokCsvLine.getEditedAt().withSecond(0));

        performEqualityCheckOnGeoLocation(didokCsvLine, atlasCsvLine);
    }

    private static void performEqualityCheckOnGeoLocation(ServicePointCsvModel didokCsvLine,
                                                          ServicePointVersionCsvModel atlasCsvLine) {
        //    assertThat(atlasCsvLine.getIsoCoutryCode()).isEqualTo(didokCsvLine.getIsoCountryCode());
        // TODO: Mapping -> CantonName in Atlas CSV is missing
        // cantonName	KANTONSNAME

        assertThat(atlasCsvLine.getCantonAbbreviation()).isEqualTo(didokCsvLine.getKantonsKuerzel());
        assertThat(atlasCsvLine.getDistrictName()).isEqualTo(didokCsvLine.getBezirksName());

        // TODO: Mapping -> cantonFsoNumber is missing in ATLAS-CSV, or is it the fsoNumber?
        //assertThat(atlasCsvLine.getFsoNumber()).isEqualTo(didokCsvLine.getKantonsNum());

        // TODO: Commented out, because districtFsoName is 0 and bezirksNum is null
        // assertThat(atlasCsvLine.getDistrictFsoName()).isEqualTo(didokCsvLine.getBezirksNum());

        assertThat(atlasCsvLine.getMunicipalityName()).isEqualTo(didokCsvLine.getGemeindeName());

        // TODO: Commented out, because getFsoNumber is 0 and getBfsNummer is null
        // assertThat(atlasCsvLine.getFsoNumber()).isEqualTo(didokCsvLine.getBfsNummer());

        assertThat(atlasCsvLine.getLocalityName()).isEqualTo(didokCsvLine.getOrtschaftsName());

        assertThat(round(atlasCsvLine.getLv95East(), 5)).isEqualTo(didokCsvLine.getELv95());
        assertThat(round(atlasCsvLine.getLv95North(), 5)).isEqualTo(didokCsvLine.getNLv95());

        // TODO: Fix rounding
//        assertThat(round(atlasCsvLine.getWgs84East(), 11)).isEqualTo(didokCsvLine.getEWgs84());
//        assertThat(round(atlasCsvLine.getWgs84North(), 11)).isEqualTo(didokCsvLine.getNWgs84());

        assertThat(round(atlasCsvLine.getWgs84WebEast(), 5)).isEqualTo(didokCsvLine.getEWgs84web());
        assertThat(round(atlasCsvLine.getWgs84WebNorth(), 5)).isEqualTo(didokCsvLine.getNWgs84web());
        assertThat(round(atlasCsvLine.getHeight(), 5)).isEqualTo(didokCsvLine.getHeight());
    }

    private LocalDateTime fromString(String string) {
        return LocalDateTime.parse(string, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
