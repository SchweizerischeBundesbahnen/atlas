package ch.sbb.atlas.servicepointdirectory.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@RequiredArgsConstructor
public enum OperatingPointType {

    // TODO: assign hasTimetable correctly
    TARIFF_POINT(false, "Tarifpunkt", "Point tarifaire", "Punto tariffale", "Tarifpunkt", "verkehrspunkt,tarifstelle", null, null, null, null),
    INVENTORY_POINT(false, "Inventarpunkt", "Point d'inventaire", "punto di inventario", "Inventarpunkt", "betriebspunkt", null, null, null, null),
    RAILNET_POINT(false, "BP Netze", "Point de réseau", "Punto di rete", "BP Netze", "betriebspunkt", null, null, null, null),
    ROUTE_SPEED_CHANGE(false, "Streckengeschwindigkeitswechsel", "Changement de vitesse de ligne", "Variazioni di velocità di linea", "Streckengeschwindigkeitswechsel", "ohnefahrplan", null, null, null, null),
    STOP_POINT(true, "Haltestelle", "Arrêt", "Fermata", "Haltestelle", "haltestelle,verkehrspunkt", null, null, null, null),
    SYSTEM_OPERATING_POINT(false, "System Betriebspunkt", "Point d’exploitation système", "Punto d’esercizio sistema", "System Betriebspunkt", "betriebspunkt", null, null, null, null),
    PROPERTY_LINE(false, "Eigentumsgrenze", "Limite de propriété", "Confine di proprietà", "Eigentumsgrenze", "technischerfahrplan", "EGr", "lpr", "cpr", null),
    INVESTMENT_LOCATIONS_OPERATING_POINT(false, "BP für Anlagestandorte DfA", "Point d’exploitations pour des installations fixes", "Punto d’esercizio per gli impianti fissi DfA", "BP für Anlagestandorte DfA", "technischerfahrplan", "BDPfA", "PEDfA", "PEDfA", null),
    COUNTRY_BORDER(false, "Landesgrenze", "Frontière nationale", "Confine ", "Landesgrenze", "technischerfahrplan", "LGr", "frn", "con", null),
    UNKNOWN(false, "Nicht spezifiziert", "Non spécifié", "Non spezificato", "Nicht spezifiziert", "technischerfahrplan", "ns", "ns", "ns", null),
    OPERATING_POINT_BUS(false, "Betriebspunkt Bus", "Point d’exploitation bus", "Punto d’esercizio bus", "Betriebspunkt Bus", "technischerfahrplan", "BPB", "PEb", "PEb", null),
    TURNING_LOOP(false, "Wendeschlaufe", "Boucle de retournement", "Cappio di ritorno", "Wendeschlaufe", "technischerfahrplan", "Wds", "bcl", "cdr", null),
    ASSIGNED_OPERATING_POINT(false, "Zugeordneter Betriebspunkt", "Point d’exploitation associé", "Punto d’esercizio associato", "Zugeordneter Betriebspunkt", "technischerfahrplan", "zBP", "PEa", "PEa", null),
    BLOCKING_POINT(false, "Blockstelle", "Poste de block", "Posto di blocco", "Blockstelle", "technischerfahrplan", "B", "B", "B", null),
    END_OF_TRACK(false, "Gleisende ", "Cul-de-sac", "Fine della traccia", "Gleisende", "technischerfahrplan", "Ge", "cds", "fitra", null),
    LANE_SEPARATION(false, "Spurtrennung ", "Tracés séparés", "Separazione dei binari", "Spurtrennung", "technischerfahrplan", "Sptr", "trsép", "sepbi", null),
    INTERSECTION(false, "Ausweiche", "Évitement", "Incrocio", "Ausweiche", "technischerfahrplan", "Ausw", "évit", "incr", null),
    CONNECTING_POINT(false, "Anschlusspunkt", "Point de raccordement", "Punto di raccordo ", "Anschlusspunkt", "technischerfahrplan", "Apt", "prc", "prc", null),
    LANGE_CHANGE(false, "Spurwechsel", "Diagonale d'échange", "Cambio binario ", "Spurwechsel", "technischerfahrplan", "Spw", "diag", "c bin", null),
    BRANCH(false, "Abzweigung, Verzweigung, Spaltweiche", "Bifurcation", "Diramazione", "Abzweigung, Verzweigung, Spaltweiche ", "technischerfahrplan", "Vzw", "bif", "drm", null),
    SERVICE_STATION(false, "Dienststation", "Station de service", "Stazione di servizio", "Dienststation", "technischerfahrplan", "Dsta", "stas", "stas", null),
    ERROR_PROFILE(false, "Fehlerprofil/Kilometersprung", "Profil corrigé/Saut kilométrique", "Profilo errore/Salto chilometrico", "Fehlerprofil", "technischerfahrplan", "FP", "PC", "PE", null),
    EX_STOP_POINT(false, "Ehemalige Haltestelle mit Infrastruktur", "Ancien arrêt avec infrastructure", "Precedente fermata con infrastruttura", "Haltestelle ausser Betrieb", "technischerfahrplan", "Hab", "aai", "pfi", null),

    ;


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
}
