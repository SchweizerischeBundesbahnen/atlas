package ch.sbb.atlas.servicepointdirectory.migration;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
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

    performEqualityCheckOnGeoLocation(didokCsvLine, atlasCsvLine);

    //    assertThat(atlasCsvLine.businessOrganisationOrganisationNumber).isEqualTo(didokCsvLine.GO_NUMMER);
    //    assertThat(atlasCsvLine.businessOrganisation.abbreviationDe).isEqualTo(didokCsvLine.GO_ABKUERZUNG_DE);
    //    assertThat(atlasCsvLine.businessOrganisation.abbreviationFr).isEqualTo(didokCsvLine.GO_ABKUERZUNG_FR);
    //    assertThat(atlasCsvLine.businessOrganisation.abbreviationIt).isEqualTo(didokCsvLine.GO_ABKUERZUNG_IT);
    //    assertThat(atlasCsvLine.businessOrganisation.abbreviationEn).isEqualTo(didokCsvLine.GO_ABKUERZUNG_EN);
    //    assertThat(atlasCsvLine.businessOrganisation.descriptionDe).isEqualTo(didokCsvLine.GO_BEZEICHNUNG_DE);
    //    assertThat(atlasCsvLine.businessOrganisation.descriptionFr).isEqualTo(didokCsvLine.GO_BEZEICHNUNG_FR);
    //    assertThat(atlasCsvLine.businessOrganisation.descriptionIt).isEqualTo(didokCsvLine.GO_BEZEICHNUNG_IT);
    //    assertThat(atlasCsvLine.businessOrganisation.descriptionEn).isEqualTo(didokCsvLine.GO_BEZEICHNUNG_EN);

    //    assertThat(atlasCsvLine.operatingPointTypeCode).isEqualTo(didokCsvLine.BP_BETRIEBSPUNKT_ART_ID);
    //    assertThat(atlasCsvLine.operatingPointTechnicalTimetableTypeCode).isEqualTo(didokCsvLine.BPTF_BETRIEBSPUNKT_ART_ID);
    //    assertThat(atlasCsvLine.meansOfTransportCode).isEqualTo(didokCsvLine.BPVH_VERKEHRSMITTEL);
    //    assertThat(atlasCsvLine.categoriesCode).isEqualTo(didokCsvLine.DS_KATEGORIEN_IDS);
    //    assertThat(atlasCsvLine.operatingPointTrafficPointTypeCode).isEqualTo(didokCsvLine.BPVB_BETRIEBSPUNKT_ART_ID);
    //    assertThat(atlasCsvLine.operatingPointRouteNetwork).isEqualTo(didokCsvLine.IS_BPS);
    //    assertThat(atlasCsvLine.operatingPointKilometer).isEqualTo(didokCsvLine.IS_BPK);
    //    assertThat(atlasCsvLine.operatingPointKilometerMasterNumber).isEqualTo(didokCsvLine.BPK_MASTER);
    //    assertThat(atlasCsvLine.sort_code_of_destination_station).isEqualTo(didokCsvLine.RICHTPUNKT_CODE);
    //    assertThat(atlasCsvLine.sboid).isEqualTo(didokCsvLine.IDENTIFIKATION);

    //assertThat(atlasCsvLine.fotComment).isEqualTo(didokCsvLine.BAV_BEMERKUNG);

    //assertThat(atlasCsvLine.creationDate).isEqualTo(didokCsvLine.ERSTELLT_AM);
    //assertThat(atlasCsvLine.editionDate).isEqualTo(didokCsvLine.GEAENDERT_AM);

  }

  private static void performEqualityCheckOnGeoLocation(ServicePointCsvModel didokCsvLine,
      ServicePointVersionCsvModel atlasCsvLine) {
    //    assertThat(atlasCsvLine.getIsoCoutryCode()).isEqualTo(didokCsvLine.getIsoCountryCode());
    // TODO: Mapping -> CantonName in Atlas CSV is missing
    // cantonName	KANTONSNAME


    assertThat(atlasCsvLine.getCantonAbbreviation()).isEqualTo(didokCsvLine.getKantonsKuerzel());

    // TODO: Commented out, because of UT8-Encoding problems
    // assertThat(atlasCsvLine.getDistrictName()).isEqualTo(didokCsvLine.getBezirksName());

    // TODO: Mapping -> cantonFsoNumber is missing in ATLAS-CSV, or is it the fsoNumber?
    //assertThat(atlasCsvLine.getFsoNumber()).isEqualTo(didokCsvLine.getKantonsNum());

    // TODO: Commented out, because districtFsoName is 0 and bezirksNum is null
    // assertThat(atlasCsvLine.getDistrictFsoName()).isEqualTo(didokCsvLine.getBezirksNum());

    // TODO: Commented out, because of UT8-Encoding problems
    // assertThat(atlasCsvLine.getMunicipalityName()).isEqualTo(didokCsvLine.getGemeindeName());

    // TODO: Commented out, because getFsoNumber is 0 and getBfsNummer is null
    // assertThat(atlasCsvLine.getFsoNumber()).isEqualTo(didokCsvLine.getBfsNummer());

    // TODO: Commented out, because of UT8-Encoding problems
    // assertThat(atlasCsvLine.getLocalityName()).isEqualTo(didokCsvLine.getOrtschaftsName());

    //    assertThat(atlasCsvLine.lv95.east).isEqualTo(didokCsvLine.E_LV95);
    //    assertThat(atlasCsvLine.lv95.north).isEqualTo(didokCsvLine.N_LV95);
    //    assertThat(atlasCsvLine.wgs84.east).isEqualTo(didokCsvLine.E_WGS84);
    //    assertThat(atlasCsvLine.wgs84.north).isEqualTo(didokCsvLine.N_WGS84);
    //    assertThat(atlasCsvLine.wgs84web.east).isEqualTo(didokCsvLine.E_WGS84WEB);
    //    assertThat(atlasCsvLine.wgs84web.north).isEqualTo(didokCsvLine.N_WGS84WEB);
    //    assertThat(atlasCsvLine.height).isEqualTo(didokCsvLine.HEIGHT);
  }
}
