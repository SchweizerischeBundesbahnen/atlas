package ch.sbb.atlas.servicepointdirectory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true)
@RequiredArgsConstructor
@Getter
public enum Category {

    NOVA_VIRTUAL("NOVA virtuell", "NOVA virtuel", "NOVA virtuale", "NOVA virtual", "Virtuelle Dienststellen für NOVA"),
    BILLETING_MACHINE("Billettautomat SBB", "Billettautomat SBB", "Billettautomat SBB", "Billettautomat SBB", "Billettautomat SBB"),
    PARK_AND_RAIL("P+Rail", "P+Rail", "P+Rail", "P+Rail", "P+Rail Anlagen genutzt von NOVA"),
    MAINTENANCE_POINT("Unterhaltstelle", "Point d'entretien", "Punto di manutenzione", "Unterhaltstelle", "Unterhaltstelle"),
    BORDER_POINT("Grenzpunkt (UIC)", "Point de frontière (UIC)", "Punto di confine (UIC)", "Grenzpunkt (UIC)", "Grenzpunkt (UIC)"),
    TCV_PASSENGER_TRANSPORT("TCV Personenverkehr", "TCV trafic voyageurs", "TCV traffico viaggiatori", "TCV Personenverkehr", "TCV Personenverkehr"),
    HIGH_VOLTAGE_AREA("Hochspannungsareal", "Installation à haute tension", "Impianto ad alta tensione", "Hochspannungsareal", "Hochspannungsareal"),
    GSMR_POLE("GSM-R Mast", "Poteau GSM-R", "Asta GSM-R", "GSM-R Mast", "GSM-R Mast"),
    POINT_OF_SALE("Verkaufsstelle", "Point de vente", "Punto vendita", "Verkaufsstelle", "Verkaufsstelle"),
    DISTRIBUTION_POINT("Vertriebspunkt", "Point de distribution", "Punto di distribuzione", "Vertriebspunkt", "Vertriebspunkt"),
    PROTECTED_PATH("Schutzstrecke", "Section de protection", "Tratta di protezione", "Schutzstrecke", "Schutzstrecke"),
    GSMR("GSM-R", "GSM-R", "GSM-R", "GSM-R", "GSM-R"),
    HOSTNAME("Hostname", "Nom d'hôte", "Hostname", "Hostname", "Hostname"),
    SIGNAL_BOX("Stellwerk", "Poste d’enclenchement", "Apparato centrale", "Stellwerk", "Stellwerk"),
    IP_CLEAN_UP("IP Bereinigung", "IP Bereinigung ", "IP Bereinigung", "IP Bereinigung", "IP Bereinigung"),
    GALLERY("Tunnel", "Tunnel", "Tunnel", "Tunnel", "Tunnel"),
    MIGRATION_DIVERSE("Migr. (alt Uhst Diverse)", "Migr. (alt Uhst Diverse)", "Migr. (alt Uhst Diverse)", "Migr. (alt Uhst Diverse)", "Migration (alt Uhst Diverse)"),
    MIGRATION_CENTRAL_SERVICE("Migr. (alt Uhst Zentr. Dienst)", "Migr. (alt Uhst Zentr. Dienst)", "Migr. (alt Uhst Zentr. Dienst)", "Migr. (alt Uhst Zentr. Dienst)", "Migration (alt Uhst Zentrale Dienste)"),
    MIGRATION_MOBILE_EQUIPE("Migr. (alt Uhst Mobile Equipe)", "Migr. (alt Uhst Mobile Equipe)", "Migr. (alt Uhst Mobile Equipe)", "Migr. (alt Uhst Mobile Equipe)", "Migration (alt Uhst Mobile Equipe)"),
    MIGRATION_TCV_PV("Migr. (alt Uhst TCV PV)", "Migr. (alt Uhst TCV PV)", "Migr. (alt Uhst TCV PV)", "Migr. (alt Uhst TCV PV)", "Migration (alt Uhst TCV Personenverkehr)"),
    TRAVEL_AGENCY("Reisebüro", "Agence de voyage", "Agenzia di viaggi", "Travel Agency", null),
    TRAVEL_AGENCY_ORGANISATION("Reisebüro Organisation", "Organisation de l'agence de vo", "Organizzazione delle agenzie d", "Travel Agency Organization", null),

    ;

    private final String designationDe;
    private final String designationFr;
    private final String designationIt;
    private final String designationEn;
    private final String description;

}
