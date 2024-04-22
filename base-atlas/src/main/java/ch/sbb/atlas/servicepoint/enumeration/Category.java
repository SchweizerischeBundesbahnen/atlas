package ch.sbb.atlas.servicepoint.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "POINT_OF_SALE")
@RequiredArgsConstructor
@Getter
public enum Category {

  NOVA_VIRTUAL(19, "NOVA virtuell", "NOVA virtuel", "NOVA virtuale", "NOVA virtual", "Virtuelle Dienststellen für NOVA"),
  BILLETING_MACHINE(20, "Billettautomat SBB", "Billettautomat SBB", "Billettautomat SBB", "Ticket machine SBB", "Billettautomat "
      + "SBB"),
  PARK_AND_RAIL(23, "P+Rail", "P+Rail", "P+Rail", "P+Rail", "P+Rail Anlagen genutzt von NOVA"),
  MAINTENANCE_POINT(1, "Unterhaltstelle", "Point d'entretien", "Punto di manutenzione", "Maintenance place", "Unterhaltstelle"),
  BORDER_POINT(18, "Grenzpunkt (UIC)", "Point de frontière (UIC)", "Punto di confine (UIC)", "Border point (UIC)", "Grenzpunkt "
      + "(UIC)"),
  TCV_PASSENGER_TRANSPORT(3, "TCV Personenverkehr", "TCV trafic voyageurs", "TCV traffico viaggiatori", "TCV passenger transport",
      "TCV Personenverkehr"),
  HIGH_VOLTAGE_AREA(4, "Hochspannungsareal", "Installation à haute tension", "Impianto ad alta tensione", "High voltage area",
      "Hochspannungsareal"),
  GSMR_POLE(5, "GSM-R Mast", "Poteau GSM-R", "Asta GSM-R", "GSM-R Mast", "GSM-R Mast"),
  POINT_OF_SALE(6, "Verkaufsstelle", "Point de vente", "Punto vendita", "Point of sale", "Verkaufsstelle"),
  DISTRIBUTION_POINT(7, "Vertriebspunkt", "Point de distribution", "Punto di distribuzione", "Distribution point",
      "Vertriebspunkt"),
  PROTECTED_PATH(8, "Schutzstrecke", "Section de protection", "Tratta di protezione", "Protection track", "Schutzstrecke"),
  GSMR(9, "GSM-R", "GSM-R", "GSM-R", "GSM-R", "GSM-R"),
  HOSTNAME(10, "Hostname", "Nom d'hôte", "Hostname", "Hostname", "Hostname"),
  SIGNAL_BOX(11, "Stellwerk", "Poste d’enclenchement", "Apparato centrale", "Signal box", "Stellwerk"),
  IP_CLEAN_UP(12, "IP Bereinigung", "IP Bereinigung ", "IP Pulizia", "IP cleanup", "IP Bereinigung"),
  GALLERY(13, "Tunnel", "Tunnel", "Galleria", "Tunnel", "Tunnel"),
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
  TRAVEL_AGENCY_ORGANISATION(22, "Reisebüro Organisation", "Organisation de l'agence de voyage", "Organizzazione delle agenzie "
      + "di viaggio",      "Travel Agency Organization", null);

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

}
