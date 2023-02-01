package ch.sbb.atlas.api.bodi.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(enumAsRef = true)
public enum BusinessType {

  STREET(30, "Strasse", "Route", "Strada"),
  STREET_WITHOUT_TRAFFIC(32, "Strasse ohne Verkehrsabrechnug",
      "Route sans rapport de circulation", "Strada senza rapporto sul traffico"),
  RAILROAD(10, "Eisenbahn", "Chemin de fer", "Ferrovia"),
  RAILROAD_UIC(11, "UIC Eisenbahn", "Chemin de fer UIC", "Ferrovia UIC"),
  TRAIN_WITHOUT_TRAFFIC(12, "Bahn ohne Verkehrsabrechnung",
      "Chemin de fer sans rapport de circulation", "Ferrovia senza rapporto sul traffico"),
  SHIP(20, "Schiff", "Bateau", "Nave"),
  SHIP_WITHOUT_TRAFFIC(22, "Schiff ohne Verkehrsabrechnung", "Bateau sans rapport de circulation",
      "Nave senza rapporto sul traffico"),
  AIR(45, "Luft", "Air", "Aria"),
  LEISURE_ACTIVITIES(50, "Freizeitangebot", "Loisirs", "Ricreativo"),
  TARIFF_ASSOCIATION(51, "Tarifverbund", "Communauté tarifaire", "Comunità tariffaria"),
  FAIR(52, "Messe", "Foire", "Fiera"),
  TRAVEL_AGENCY_ORGANISATION(60, "Reisebüroorganisation", "Organisation d'agence de voyages",
      "Organizzazione d'agenzia di viaggi"),
  CUSTOMER_INFORMATION(70, "Kundeninformation", "Information clientèle",
      "Informazione alla clientela"),
  SUBSIDIARY(80, "Tochtergesellschaft (Bahn)", "Filiale (Chemin de fer)", "Affiliata (Ferrovia)"),
  INTERNAL_BILLING_PURPOSES(95, "interne Abrechnungszwecke", "fins de facturation",
      "scopi di fatturazione interna"),
  UNKNOWN(99, "unbekannt", "inconnu", "sconosciuto");


  private static final String PIPE = "|";
  private final int id;
  private final String typeDe;
  private final String typeFr;
  private final String typeIt;

}
