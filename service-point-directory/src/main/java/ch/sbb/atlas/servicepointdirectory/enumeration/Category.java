package ch.sbb.atlas.servicepointdirectory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "POINT_OF_SALE")
@RequiredArgsConstructor
@Getter
public enum Category implements CodeAndDesignations {

  NOVA_VIRTUAL(19, "NOVA virtuell", "NOVA virtuel", "NOVA virtuale", "NOVA virtual", "Virtuelle Dienststellen für NOVA"),
  BILLETING_MACHINE(20, "Billettautomat SBB", "Billettautomat SBB", "Billettautomat SBB", "Billettautomat SBB", "Billettautomat "
      + "SBB"),
  PARK_AND_RAIL(23, "P+Rail", "P+Rail", "P+Rail", "P+Rail", "P+Rail Anlagen genutzt von NOVA"),
  MAINTENANCE_POINT(1, "Unterhaltstelle", "Point d'entretien", "Punto di manutenzione", "Unterhaltstelle", "Unterhaltstelle"),
  BORDER_POINT(18, "Grenzpunkt (UIC)", "Point de frontière (UIC)", "Punto di confine (UIC)", "Grenzpunkt (UIC)", "Grenzpunkt "
      + "(UIC)"),
  TCV_PASSENGER_TRANSPORT(3, "TCV Personenverkehr", "TCV trafic voyageurs", "TCV traffico viaggiatori", "TCV Personenverkehr",
      "TCV Personenverkehr"),
  HIGH_VOLTAGE_AREA(4, "Hochspannungsareal", "Installation à haute tension", "Impianto ad alta tensione", "Hochspannungsareal",
      "Hochspannungsareal"),
  GSMR_POLE(5, "GSM-R Mast", "Poteau GSM-R", "Asta GSM-R", "GSM-R Mast", "GSM-R Mast"),
  POINT_OF_SALE(6, "Verkaufsstelle", "Point de vente", "Punto vendita", "Verkaufsstelle", "Verkaufsstelle"),
  DISTRIBUTION_POINT(7, "Vertriebspunkt", "Point de distribution", "Punto di distribuzione", "Vertriebspunkt", "Vertriebspunkt"),
  PROTECTED_PATH(8, "Schutzstrecke", "Section de protection", "Tratta di protezione", "Schutzstrecke", "Schutzstrecke"),
  GSMR(9, "GSM-R", "GSM-R", "GSM-R", "GSM-R", "GSM-R"),
  HOSTNAME(10, "Hostname", "Nom d'hôte", "Hostname", "Hostname", "Hostname"),
  SIGNAL_BOX(11, "Stellwerk", "Poste d’enclenchement", "Apparato centrale", "Stellwerk", "Stellwerk"),
  IP_CLEAN_UP(12, "IP Bereinigung", "IP Bereinigung ", "IP Bereinigung", "IP Bereinigung", "IP Bereinigung"),
  GALLERY(13, "Tunnel", "Tunnel", "Tunnel", "Tunnel", "Tunnel"),
  MIGRATION_DIVERSE(14, "Migr. (alt Uhst Diverse)", "Migr. (alt Uhst Diverse)", "Migr. (alt Uhst Diverse)",
      "Migr. (alt Uhst Diverse)", "Migration (alt Uhst Diverse)"),
  MIGRATION_CENTRAL_SERVICE(15, "Migr. (alt Uhst Zentr. Dienst)", "Migr. (alt Uhst Zentr. Dienst)", "Migr. (alt Uhst Zentr. "
      + "Dienst)",
      "Migr. (alt Uhst Zentr. Dienst)", "Migration (alt Uhst Zentrale Dienste)"),
  MIGRATION_MOBILE_EQUIPE(16, "Migr. (alt Uhst Mobile Equipe)", "Migr. (alt Uhst Mobile Equipe)",
      "Migr. (alt Uhst Mobile Equipe)",
      "Migr. (alt Uhst Mobile Equipe)", "Migration (alt Uhst Mobile Equipe)"),
  MIGRATION_TCV_PV(17, "Migr. (alt Uhst TCV PV)", "Migr. (alt Uhst TCV PV)", "Migr. (alt Uhst TCV PV)", "Migr. (alt Uhst TCV PV)",
      "Migration (alt Uhst TCV Personenverkehr)"),
  TRAVEL_AGENCY(21, "Reisebüro", "Agence de voyage", "Agenzia di viaggi", "Travel Agency", null),
  TRAVEL_AGENCY_ORGANISATION(22, "Reisebüro Organisation", "Organisation de l'agence de vo", "Organizzazione delle agenzie d",
      "Travel Agency Organization", null),
  ROUTE_SPEED_CHANGE(70, "Streckengeschwindigkeitswechsel", "Changement de vitesse de ligne", "Variazioni di velocità di linea"
      , "Streckengeschwindigkeitswechsel", null),

  ;

  private final Integer id;
  private final String designationDe;
  private final String designationFr;
  private final String designationIt;
  private final String designationEn;
  private final String description;

  public static Category from(Integer id) {
    return Arrays.stream(Category.values()).filter(category -> Objects.equals(category.getId(), id)).findFirst()
        .orElseThrow(() -> new IllegalArgumentException(String.valueOf(id)));
  }

  @Override
  public String getCode() {
    return String.valueOf(id);
  }
}
