package ch.sbb.atlas.servicepointdirectory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Schema(enumAsRef = true, example = "COUNTRY_BORDER")
@Getter
@RequiredArgsConstructor
public enum OperatingPointTechnicalTimetableType implements CodeAndDesignations {

  STOP_POINT(51, true, "Haltestelle", "Arrêt", "Fermata", "Haltestelle", "haltestelle,verkehrspunkt", null, null, null, null),
  PROPERTY_LINE(43, true, "Eigentumsgrenze", "Limite de propriété", "Confine di proprietà", "Eigentumsgrenze",
      "technischerfahrplan",
      "EGr", "lpr", "cpr", null),
  INVESTMENT_LOCATIONS_OPERATING_POINT(60, true, "BP für Anlagestandorte DfA", "Point d’exploitations pour des "
      + "installations fixes",
      "Punto d’esercizio per gli impianti fissi DfA", "BP für Anlagestandorte DfA", "technischerfahrplan", "BDPfA", "PEDfA",
      "PEDfA", null),
  COUNTRY_BORDER(42, true, "Landesgrenze", "Frontière nationale", "Confine ", "Landesgrenze", "technischerfahrplan", "LGr",
      "frn",
      "con", null),
  UNKNOWN(37, true, "Nicht spezifiziert", "Non spécifié", "Non spezificato", "Nicht spezifiziert", "technischerfahrplan", "ns",
      "ns",
      "ns", null),
  OPERATING_POINT_BUS(35, true, "Betriebspunkt Bus", "Point d’exploitation bus", "Punto d’esercizio bus", "Betriebspunkt Bus",
      "technischerfahrplan", "BPB", "PEb", "PEb", null),
  TURNING_LOOP(33, true, "Wendeschlaufe", "Boucle de retournement", "Cappio di ritorno", "Wendeschlaufe", "technischerfahrplan",
      "Wds", "bcl", "cdr", null),
  ASSIGNED_OPERATING_POINT(16, true, "Zugeordneter Betriebspunkt", "Point d’exploitation associé", "Punto d’esercizio associato",
      "Zugeordneter Betriebspunkt", "technischerfahrplan", "zBP", "PEa", "PEa", null),
  BLOCKING_POINT(14, true, "Blockstelle", "Poste de block", "Posto di blocco", "Blockstelle", "technischerfahrplan", "B", "B",
      "B",
      null),
  END_OF_TRACK(13, true, "Gleisende", "Cul-de-sac", "Fine della traccia", "Gleisende", "technischerfahrplan", "Ge", "cds",
      "fitra",
      null),
  LANE_SEPARATION(12, true, "Spurtrennung", "Tracés séparés", "Separazione dei binari", "Spurtrennung", "technischerfahrplan",
      "Sptr", "trsép", "sepbi", null),
  INTERSECTION(11, true, "Ausweiche", "Évitement", "Incrocio", "Ausweiche", "technischerfahrplan", "Ausw", "évit", "incr", null),
  CONNECTING_POINT(9, true, "Anschlusspunkt", "Point de raccordement", "Punto di raccordo ", "Anschlusspunkt",
      "technischerfahrplan", "Apt", "prc", "prc", null),
  LANE_CHANGE(8, true, "Spurwechsel", "Diagonale d'échange", "Cambio binario ", "Spurwechsel", "technischerfahrplan", "Spw",
      "diag", "c bin", null),
  BRANCH(7, true, "Abzweigung, Verzweigung, Spaltweiche", "Bifurcation", "Diramazione", "Abzweigung, Verzweigung, Spaltweiche ",
      "technischerfahrplan", "Vzw", "bif", "drm", null),
  SERVICE_STATION(3, true, "Dienststation", "Station de service", "Stazione di servizio", "Dienststation", "technischerfahrplan",
      "Dsta", "stas", "stas", null),
  ERROR_PROFILE(32, true, "Fehlerprofil/Kilometersprung", "Profil corrigé/Saut kilométrique",
      "Profilo errore/Salto chilometrico",
      "Fehlerprofil", "technischerfahrplan", "FP", "PC", "PE", null),
  EX_STOP_POINT(15, true, "Ehemalige Haltestelle mit Infrastruktur", "Ancien arrêt avec infrastructure",
      "Precedente fermata con infrastruttura", "Haltestelle ausser Betrieb", "technischerfahrplan", "Hab", "aai", "pfi", null),
  ROUTE_SPEED_CHANGE(70, true, "Streckengeschwindigkeitswechsel", "Changement de vitesse de ligne",
      "Variazioni di velocità di linea", "Streckengeschwindigkeitswechsel", "technischerfahrplan", "Stkmh", "Stkmh", "Stkmh",
      null),

  ;

  private final Integer id;
  @Accessors(fluent = true)
  private final boolean hasTimetable;
  private final String designationDe;
  private final String designationFr;
  private final String designationIt;
  private final String designationEn;
  private final String allowedFeatures;
  private final String abbreviationDe;
  private final String abbreviationFr;
  private final String abbreviationIt;
  private final String abbreviationEn;

  public static OperatingPointTechnicalTimetableType from(Integer id) {
    return Arrays.stream(OperatingPointTechnicalTimetableType.values()).filter(operatingPointType -> Objects.equals(operatingPointType.getId(), id))
        .findFirst().orElse(null);
  }

  @Override
  public String getCode() {
    return String.valueOf(id);
  }
}
