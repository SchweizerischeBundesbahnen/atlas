package ch.sbb.atlas.servicepointdirectory.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServicePointCsvModel {

  @JsonProperty("NUMMER")
  private Integer NUMMER;

  @JsonProperty("LAENDERCODE")
  private Integer LAENDERCODE;

  @JsonProperty("COUNTRYCODE")
  private String COUNTRYCODE;

  @JsonProperty("UIC_NUMMER")
  private Integer UIC_NUMMER;

  @JsonProperty("DIDOK_CODE")
  private Integer DIDOK_CODE;

  @JsonProperty("GUELTIG_VON")
  private String GUELTIG_VON; // TODO: date

  @JsonProperty("GUELTIG_BIS")
  private String GUELTIG_BIS; // TODO: date

  @JsonProperty("STATUS")
  private Integer STATUS; // TODO: enum

  @JsonProperty("BEZEICHNUNG_OFFIZIELL")
  private String BEZEICHNUNG_OFFIZIELL;

  @JsonProperty("BEZEICHNUNG_LANG")
  private String BEZEICHNUNG_LANG;

  @JsonProperty("ABKUERZUNG")
  private String ABKUERZUNG;

  @JsonProperty("IS_BETRIEBSPUNKT")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean IS_BETRIEBSPUNKT;

  @JsonProperty("IS_FAHRPLAN")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean IS_FAHRPLAN;

  @JsonProperty("IS_HALTESTELLE")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean IS_HALTESTELLE;

  @JsonProperty("IS_BEDIENPUNKT")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean IS_BEDIENPUNKT;

  @JsonProperty("IS_VERKEHRSPUNKT")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean IS_VERKEHRSPUNKT;

  @JsonProperty("IS_GRENZPUNKT")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean IS_GRENZPUNKT;

  @JsonProperty("IS_VIRTUELL")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean IS_VIRTUELL;

  @JsonProperty("ORTSCHAFTSNAME")
  private String ORTSCHAFTSNAME;

  @JsonProperty("GEMEINDENAME")
  private String GEMEINDENAME;

  @JsonProperty("BFS_NUMMER")
  private Integer BFS_NUMMER;

  @JsonProperty("BEZIRKSNAME")
  private String BEZIRKSNAME;

  @JsonProperty("BEZIRKSNUM")
  private Integer BEZIRKSNUM;

  @JsonProperty("KANTONSNAME")
  private String KANTONSNAME;

  @JsonProperty("KANTONSNUM")
  private Integer KANTONSNUM;

  @JsonProperty("LAND_ISO2_GEO")
  private String LAND_ISO2_GEO;

  @JsonProperty("VERANTWORTLICHE_GO_ID")
  private String VERANTWORTLICHE_GO_ID;

  @JsonProperty("IDENTIFIKATION")
  private Integer IDENTIFIKATION;

  @JsonProperty("ID")
  private String ID;

  @JsonProperty("ERSTELLT_AM")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime ERSTELLT_AM;

  @JsonProperty("GEAENDERT_AM")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime GEAENDERT_AM;

  @JsonProperty("BP_ART_BEZEICHNUNG_DE")
  private String BP_ART_BEZEICHNUNG_DE;

  @JsonProperty("BP_ART_BEZEICHNUNG_FR")
  private String BP_ART_BEZEICHNUNG_FR;

  @JsonProperty("BP_ART_BEZEICHNUNG_IT")
  private String BP_ART_BEZEICHNUNG_IT;

  @JsonProperty("BP_ART_BEZEICHNUNG_EN")
  private String BP_ART_BEZEICHNUNG_EN;

  @JsonProperty("BP_BETRIEBSPUNKT_ART_ID")
  private Integer BP_BETRIEBSPUNKT_ART_ID;

  @JsonProperty("BPOF_ART_BEZEICHNUNG_DE")
  private String BPOF_ART_BEZEICHNUNG_DE;

  @JsonProperty("BPOF_ART_BEZEICHNUNG_FR")
  private String BPOF_ART_BEZEICHNUNG_FR;

  @JsonProperty("BPOF_ART_BEZEICHNUNG_IT")
  private String BPOF_ART_BEZEICHNUNG_IT;

  @JsonProperty("BPOF_ART_BEZEICHNUNG_EN")
  private String BPOF_ART_BEZEICHNUNG_EN;

  @JsonProperty("BPOF_BETRIEBSPUNKT_ART_ID")
  private Integer BPOF_BETRIEBSPUNKT_ART_ID;

  @JsonProperty("IS_BPS")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean IS_BPS;

  @JsonProperty("IS_BPK")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean IS_BPK;

  @JsonProperty("BPK_MASTER")
  private Integer BPK_MASTER;

  @JsonProperty("BPTF_ART_BEZEICHNUNG_DE")
  private String BPTF_ART_BEZEICHNUNG_DE;

  @JsonProperty("BPTF_ART_BEZEICHNUNG_FR")
  private String BPTF_ART_BEZEICHNUNG_FR;

  @JsonProperty("BPTF_ART_BEZEICHNUNG_IT")
  private String BPTF_ART_BEZEICHNUNG_IT;

  @JsonProperty("BPTF_ART_BEZEICHNUNG_EN")
  private String BPTF_ART_BEZEICHNUNG_EN;

  @JsonProperty("BPTF_BETRIEBSPUNKT_ART_ID")
  private Integer BPTF_BETRIEBSPUNKT_ART_ID;

  @JsonProperty("IS_CONTAINER_HANDLING")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean IS_CONTAINER_HANDLING;

  @JsonProperty("CRDCODE")
  private Integer CRDCODE;

  @JsonProperty("NAME")
  private String NAME;

  @JsonProperty("NAME_ASCII")
  private String NAME_ASCII;

  @JsonProperty("NUTS_CODE_ID")
  private String NUTS_CODE_ID;

  @JsonProperty("RESPONSIBLE_IM")
  private Integer RESPONSIBLE_IM;

  @JsonProperty("DESCRIPTION")
  private String DESCRIPTION;

  @JsonProperty("BEZEICHNUNG_17")
  private String BEZEICHNUNG_17;

  @JsonProperty("BEZEICHNUNG_35")
  private String BEZEICHNUNG_35;

  @JsonProperty("OEFFNUNGSBEDINGUNG")
  private String OEFFNUNGSBEDINGUNG; // TODO: enum

  @JsonProperty("RESA_BEDINGUNG")
  private String RESA_BEDINGUNG; // TODO

  @JsonProperty("WAGENETIKETTE")
  private String WAGENETIKETTE;

  @JsonProperty("ZOLL_CODE")
  private String ZOLL_CODE; // TODO

  @JsonProperty("RICHTPUNKT_CODE")
  private String RICHTPUNKT_CODE;

  @JsonProperty("BPVB_ART_BEZEICHNUNG_DE")
  private String BPVB_ART_BEZEICHNUNG_DE;

  @JsonProperty("BPVB_ART_BEZEICHNUNG_FR")
  private String BPVB_ART_BEZEICHNUNG_FR;

  @JsonProperty("BPVB_ART_BEZEICHNUNG_IT")
  private String BPVB_ART_BEZEICHNUNG_IT;

  @JsonProperty("BPVB_ART_BEZEICHNUNG_EN")
  private String BPVB_ART_BEZEICHNUNG_EN;

  @JsonProperty("BPVB_BETRIEBSPUNKT_ART_ID")
  private Integer BPVB_BETRIEBSPUNKT_ART_ID; // TODO: enum?

  @JsonProperty("BPVH_BEZEICHNUNG_ALT")
  private String BPVH_BEZEICHNUNG_ALT;

  @JsonProperty("BPVH_EPR_CODE")
  private String BPVH_EPR_CODE; // TODO

  @JsonProperty("BPVH_IATA_CODE")
  private String BPVH_IATA_CODE; // TODO

  @JsonProperty("BPVH_VERKEHRSMITTEL")
  private String BPVH_VERKEHRSMITTEL;

  @JsonProperty("BPVH_VERKEHRSMITTEL_TEXT_DE")
  private String BPVH_VERKEHRSMITTEL_TEXT_DE;

  @JsonProperty("BPVH_VERKEHRSMITTEL_TEXT_FR")
  private String BPVH_VERKEHRSMITTEL_TEXT_FR;

  @JsonProperty("BPVH_VERKEHRSMITTEL_TEXT_IT")
  private String BPVH_VERKEHRSMITTEL_TEXT_IT;

  @JsonProperty("BPVH_VERKEHRSMITTEL_TEXT_EN")
  private String BPVH_VERKEHRSMITTEL_TEXT_EN;

  @JsonProperty("MIN_GUELTIG_VON")
  private String MIN_GUELTIG_VON; // TODO: date

  @JsonProperty("MAX_GUELTIG_BIS")
  private String MAX_GUELTIG_BIS; // TODO: date

  @JsonProperty("KANTONSKUERZEL")
  private String KANTONSKUERZEL;

  @JsonProperty("GO_NUMMER")
  private Integer GO_NUMMER;

  @JsonProperty("GO_ABKUERZUNG_DE")
  private String GO_ABKUERZUNG_DE;

  @JsonProperty("GO_ABKUERZUNG_FR")
  private String GO_ABKUERZUNG_FR;

  @JsonProperty("GO_ABKUERZUNG_IT")
  private String GO_ABKUERZUNG_IT;

  @JsonProperty("GO_ABKUERZUNG_EN")
  private String GO_ABKUERZUNG_EN;

  @JsonProperty("GO_BEZEICHNUNG_DE")
  private String GO_BEZEICHNUNG_DE;

  @JsonProperty("GO_BEZEICHNUNG_FR")
  private String GO_BEZEICHNUNG_FR;

  @JsonProperty("GO_BEZEICHNUNG_IT")
  private String GO_BEZEICHNUNG_IT;

  @JsonProperty("GO_BEZEICHNUNG_EN")
  private String GO_BEZEICHNUNG_EN;

  @JsonProperty("DS_KATEGORIEN_IDS")
  private String DS_KATEGORIEN_IDS;

  @JsonProperty("DS_KATEGORIEN_DE")
  private String DS_KATEGORIEN_DE;

  @JsonProperty("DS_KATEGORIEN_FR")
  private String DS_KATEGORIEN_FR;

  @JsonProperty("DS_KATEGORIEN_IT")
  private String DS_KATEGORIEN_IT;

  @JsonProperty("DS_KATEGORIEN_EN")
  private String DS_KATEGORIEN_EN;

  @JsonProperty("BAV_BEMERKUNG")
  private String BAV_BEMERKUNG;

  @JsonProperty("OST")
  private Double OST;

  @JsonProperty("NORD")
  private Double NORD;

  @JsonProperty("HEIGHT")
  private Double HEIGHT;

  @JsonProperty("SLOID")
  private String SLOID;

  @JsonProperty("E_LV03")
  private Double E_LV03;

  @JsonProperty("N_LV03")
  private Double N_LV03;

  @JsonProperty("E_LV95")
  private Double E_LV95;

  @JsonProperty("N_LV95")
  private Double N_LV95;

  @JsonProperty("E_WGS84")
  private Double E_WGS84;

  @JsonProperty("N_WGS84")
  private Double N_WGS84;

  @JsonProperty("E_WGS84WEB")
  private Double E_WGS84WEB;

  @JsonProperty("N_WGS84WEB")
  private Double N_WGS84WEB;

  @JsonProperty("IS_GEOMETRY_EMPTY")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean IS_GEOMETRY_EMPTY;

  @JsonProperty("HTYP_ID")
  private Integer HTYP_ID; // TODO

  @JsonProperty("HTYP_BESCHREIBUNG_DE")
  private String HTYP_BESCHREIBUNG_DE;

  @JsonProperty("HTYP_BESCHREIBUNG_FR")
  private String HTYP_BESCHREIBUNG_FR;

  @JsonProperty("HTYP_BESCHREIBUNG_IT")
  private String HTYP_BESCHREIBUNG_IT;

  @JsonProperty("HTYP_BESCHREIBUNG_EN")
  private String HTYP_BESCHREIBUNG_EN;

  @JsonProperty("HTYP_ABKUERZUNG_DE")
  private String HTYP_ABKUERZUNG_DE;

  @JsonProperty("HTYP_ABKUERZUNG_FR")
  private String HTYP_ABKUERZUNG_FR;

  @JsonProperty("HTYP_ABKUERZUNG_IT")
  private String HTYP_ABKUERZUNG_IT;

  @JsonProperty("HTYP_ABKUERZUNG_EN")
  private String HTYP_ABKUERZUNG_EN;

  @JsonProperty("HTYP_ANHOERUNG")
  private Integer HTYP_ANHOERUNG; // TODO

  @JsonProperty("HTYP_IS_AKTIV")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean HTYP_IS_AKTIV; // TODO

  @JsonProperty("HTYP_IS_SICHTBAR")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean HTYP_IS_SICHTBAR; // TODO

  @JsonProperty("MAPPING_CRDCODE")
  private String MAPPING_CRDCODE;

  @JsonProperty("MAPPING_EVAPLUS")
  private String MAPPING_EVAPLUS; // TODO

  @JsonProperty("MAPPING_IFOPT")
  private String MAPPING_IFOPT; // TODO

  @JsonProperty("MAPPING_FUTURE_ID1")
  private String MAPPING_FUTURE_ID1; // TODO

  @JsonProperty("MAPPING_FUTURE_ID2")
  private String MAPPING_FUTURE_ID2; // TODO

  @JsonProperty("MAPPING_FUTURE_ID3")
  private String MAPPING_FUTURE_ID3; // TODO

  @JsonProperty("MAPPING_UICCODE")
  private Integer MAPPING_UICCODE;

  @JsonProperty("LETZTER_CRD_UPDATE")
  private String LETZTER_CRD_UPDATE; // TODO

  @JsonProperty("TU_ABKUERZUNG")
  private String TU_ABKUERZUNG;

  @JsonProperty("TU_AMTLICHE_BEZEICHNUNG")
  private String TU_AMTLICHE_BEZEICHNUNG;

  @JsonProperty("TU_NUMMER")
  private String TU_NUMMER;

  @JsonProperty("TU_HR_NAME")
  private String TU_HR_NAME;

  @JsonProperty("TU_UNTERNEHMENS_ID")
  private String TU_UNTERNEHMENS_ID;

  @JsonProperty("ERSTELLT_VON")
  private String ERSTELLT_VON;

  @JsonProperty("GEAENDERT_VON")
  private String GEAENDERT_VON;

}
