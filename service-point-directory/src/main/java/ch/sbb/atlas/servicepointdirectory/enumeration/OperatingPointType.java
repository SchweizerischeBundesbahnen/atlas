package ch.sbb.atlas.servicepointdirectory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Schema(enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum OperatingPointType {

  // TODO: assign hasTimetable correctly
  TARIFF_POINT(50, false, "Tarifpunkt", "Point tarifaire", "Punto tariffale", "Tarifpunkt", "verkehrspunkt,tarifstelle", null,
      null,
      null, null),
  INVENTORY_POINT(30, false, "Inventarpunkt", "Point d'inventaire", "punto di inventario", "Inventarpunkt", "betriebspunkt", null,
      null, null, null),
  RAILNET_POINT(31, false, "BP Netze", "Point de réseau", "Punto di rete", "BP Netze", "betriebspunkt", null, null, null, null),
  ROUTE_SPEED_CHANGE(10, false, "Streckengeschwindigkeitswechsel", "Changement de vitesse de ligne",
      "Variazioni di velocità di linea", "Streckengeschwindigkeitswechsel", "ohnefahrplan", null, null, null, null),
  STOP_POINT(51, true, "Haltestelle", "Arrêt", "Fermata", "Haltestelle", "haltestelle,verkehrspunkt", null, null, null, null),
  SYSTEM_OPERATING_POINT(40, false, "System Betriebspunkt", "Point d’exploitation système", "Punto d’esercizio sistema",
      "System Betriebspunkt", "betriebspunkt", null, null, null, null),
  PROPERTY_LINE(43, false, "Eigentumsgrenze", "Limite de propriété", "Confine di proprietà", "Eigentumsgrenze",
      "technischerfahrplan",
      "EGr", "lpr", "cpr", null),
  INVESTMENT_LOCATIONS_OPERATING_POINT(60, false, "BP für Anlagestandorte DfA", "Point d’exploitations pour des "
      + "installations fixes",
      "Punto d’esercizio per gli impianti fissi DfA", "BP für Anlagestandorte DfA", "technischerfahrplan", "BDPfA", "PEDfA",
      "PEDfA", null),
  COUNTRY_BORDER(42, false, "Landesgrenze", "Frontière nationale", "Confine ", "Landesgrenze", "technischerfahrplan", "LGr",
      "frn",
      "con", null),
  UNKNOWN(37, false, "Nicht spezifiziert", "Non spécifié", "Non spezificato", "Nicht spezifiziert", "technischerfahrplan", "ns",
      "ns",
      "ns", null),
  OPERATING_POINT_BUS(35, false, "Betriebspunkt Bus", "Point d’exploitation bus", "Punto d’esercizio bus", "Betriebspunkt Bus",
      "technischerfahrplan", "BPB", "PEb", "PEb", null),
  TURNING_LOOP(33, false, "Wendeschlaufe", "Boucle de retournement", "Cappio di ritorno", "Wendeschlaufe", "technischerfahrplan",
      "Wds", "bcl", "cdr", null),
  ASSIGNED_OPERATING_POINT(16, false, "Zugeordneter Betriebspunkt", "Point d’exploitation associé", "Punto d’esercizio associato",
      "Zugeordneter Betriebspunkt", "technischerfahrplan", "zBP", "PEa", "PEa", null),
  BLOCKING_POINT(14, false, "Blockstelle", "Poste de block", "Posto di blocco", "Blockstelle", "technischerfahrplan", "B", "B",
      "B",
      null),
  END_OF_TRACK(13, false, "Gleisende", "Cul-de-sac", "Fine della traccia", "Gleisende", "technischerfahrplan", "Ge", "cds",
      "fitra",
      null),
  LANE_SEPARATION(12, false, "Spurtrennung", "Tracés séparés", "Separazione dei binari", "Spurtrennung", "technischerfahrplan",
      "Sptr", "trsép", "sepbi", null),
  INTERSECTION(11, false, "Ausweiche", "Évitement", "Incrocio", "Ausweiche", "technischerfahrplan", "Ausw", "évit", "incr", null),
  CONNECTING_POINT(9, false, "Anschlusspunkt", "Point de raccordement", "Punto di raccordo ", "Anschlusspunkt",
      "technischerfahrplan", "Apt", "prc", "prc", null),
  LANGE_CHANGE(8, false, "Spurwechsel", "Diagonale d'échange", "Cambio binario ", "Spurwechsel", "technischerfahrplan", "Spw",
      "diag", "c bin", null),
  BRANCH(7, false, "Abzweigung, Verzweigung, Spaltweiche", "Bifurcation", "Diramazione", "Abzweigung, Verzweigung, Spaltweiche ",
      "technischerfahrplan", "Vzw", "bif", "drm", null),
  SERVICE_STATION(3, false, "Dienststation", "Station de service", "Stazione di servizio", "Dienststation", "technischerfahrplan",
      "Dsta", "stas", "stas", null),
  ERROR_PROFILE(32, false, "Fehlerprofil/Kilometersprung", "Profil corrigé/Saut kilométrique",
      "Profilo errore/Salto chilometrico",
      "Fehlerprofil", "technischerfahrplan", "FP", "PC", "PE", null),
  EX_STOP_POINT(15, false, "Ehemalige Haltestelle mit Infrastruktur", "Ancien arrêt avec infrastructure",
      "Precedente fermata con infrastruttura", "Haltestelle ausser Betrieb", "technischerfahrplan", "Hab", "aai", "pfi", null);

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

  public static OperatingPointType from(Integer id) {
    return Arrays.stream(OperatingPointType.values()).filter(el -> Objects.equals(el.id, id)).findFirst().orElse(null);
  }
}
