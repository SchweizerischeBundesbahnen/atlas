package ch.sbb.atlas.base.service.imports.servicepoint.servicepoint;

import ch.sbb.atlas.base.service.imports.servicepoint.BaseDidokCsvModel;
import ch.sbb.atlas.base.service.imports.servicepoint.deserializer.NumericBooleanDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServicePointCsvModel extends BaseDidokCsvModel {

  @JsonProperty("NUMMER")
  private Integer nummer;

  @JsonProperty("LAENDERCODE")
  private Integer laendercode;

  @JsonProperty("DIDOK_CODE")
  private Integer didokCode;

  @JsonProperty("STATUS")
  private Integer status;

  @JsonProperty("BEZEICHNUNG_OFFIZIELL")
  private String bezeichnungOffiziell;

  @EqualsAndHashCode.Exclude
  @JsonProperty("BEZEICHNUNG_17")
  private String bezeichnung17;

  @EqualsAndHashCode.Exclude
  @JsonProperty("BEZEICHNUNG_35")
  private String bezeichnung35;

  @EqualsAndHashCode.Exclude
  @JsonProperty("OEFFNUNGSBEDINGUNG")
  private String oeffnungsBedingung;

  @JsonProperty("BEZEICHNUNG_LANG")
  private String bezeichnungLang;

  @JsonProperty("ABKUERZUNG")
  private String abkuerzung;

  @EqualsAndHashCode.Exclude
  @JsonProperty("IS_VIRTUELL")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean isVirtuell;

  @JsonProperty("ORTSCHAFTSNAME")
  private String ortschaftsName;

  @JsonProperty("GEMEINDENAME")
  private String gemeindeName;

  @JsonProperty("BFS_NUMMER")
  private Integer bfsNummer;

  @JsonProperty("BEZIRKSNAME")
  private String bezirksName;

  @JsonProperty("BEZIRKSNUM")
  private Integer bezirksNum;

  @JsonProperty("KANTONSNAME")
  private String kantonsName;

  @JsonProperty("KANTONSNUM")
  private Integer kantonsNum;

  @JsonProperty("IDENTIFIKATION")
  private String said;

  @JsonProperty("BPVB_BETRIEBSPUNKT_ART_ID")
  private Integer bpvbBetriebspunktArtId;

  @JsonProperty("BPOF_BETRIEBSPUNKT_ART_ID")
  private Integer bpofBetriebspunktArtId;

  @JsonProperty("BPTF_BETRIEBSPUNKT_ART_ID")
  private Integer bptfBetriebspunktArtId;

  @JsonProperty("BP_BETRIEBSPUNKT_ART_ID")
  private Integer bpBetriebspunktArtId;

  @JsonProperty("BPVH_VERKEHRSMITTEL")
  private String bpvhVerkehrsmittel;

  @JsonProperty("GO_NUMMER")
  private Integer goNummer;

  @JsonProperty("DS_KATEGORIEN_IDS")
  private String dsKategorienIds;

  @JsonProperty("SLOID")
  private String sloid;

  @JsonProperty("HTYP_ID")
  private Integer hTypId;

  @JsonProperty("BAV_BEMERKUNG")
  private String comment;

  @JsonProperty("RICHTPUNKT_CODE")
  private String richtpunktCode;

  @JsonProperty("IS_BPS")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean operatingPointRouteNetwork;

  @JsonProperty("BPK_MASTER")
  private Integer operatingPointKilometerMaster;

  @JsonProperty("IS_BETRIEBSPUNKT")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean isBetriebspunkt;

  @JsonProperty("IS_FAHRPLAN")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean isFahrplan;

  @JsonProperty("IS_HALTESTELLE")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean isHaltestelle;

  @JsonProperty("IS_BEDIENPUNKT")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean isBedienpunkt;

  @JsonProperty("IS_VERKEHRSPUNKT")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean isVerkehrspunkt;

  @JsonProperty("IS_GRENZPUNKT")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean isGrenzpunkt;
}
