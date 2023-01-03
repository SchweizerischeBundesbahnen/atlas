package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.service.deserializer.LocalDateTimeDeserializer;
import ch.sbb.atlas.servicepointdirectory.service.deserializer.NumericBooleanDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServicePointCsvModel {

  @JsonProperty("NUMMER")
  private Integer nummer;

  @JsonProperty("LAENDERCODE")
  private Integer laendercode;

  @JsonProperty("DIDOK_CODE")
  private Integer didokCode;

  @JsonProperty("GUELTIG_VON")
  private String gueltigVon;

  @JsonProperty("GUELTIG_BIS")
  private String gueltigBis;

  @JsonProperty("STATUS")
  private Integer status;

  @JsonProperty("BEZEICHNUNG_OFFIZIELL")
  private String bezeichnungOffiziell;

  @JsonProperty("BEZEICHNUNG_LANG")
  private String bezeichnungLang;

  @JsonProperty("ABKUERZUNG")
  private String abkuerzung;

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

  @JsonProperty("ERSTELLT_AM")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  @JsonProperty("ERSTELLT_VON")
  private String createdBy;

  @JsonProperty("GEAENDERT_AM")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime editedAt;

  @JsonProperty("GEAENDERT_VON")
  private String editedBy;

  @JsonProperty("BPVB_BETRIEBSPUNKT_ART_ID")
  private Integer bpvbBetriebspunktArtId;

  @JsonProperty("BPOF_BETRIEBSPUNKT_ART_ID")
  private Integer bpofBetriebspunktArtId;

  @JsonProperty("BPVH_VERKEHRSMITTEL")
  private String bpvhVerkehrsmittel;

  @JsonProperty("GO_NUMMER")
  private Integer goNummer;

  @JsonProperty("DS_KATEGORIEN_IDS")
  private String dsKategorienIds;

  @JsonProperty("HEIGHT")
  private Double height;

  @JsonProperty("SLOID")
  private String sloid;

  @JsonProperty("SOURCE_SPATIAL_REF")
  private SpatialReference spatialReference;

  @JsonProperty("E_LV03")
  private Double eLV03;

  @JsonProperty("N_LV03")
  private Double nLV03;

  @JsonProperty("E_LV95")
  private Double eLV95;

  @JsonProperty("N_LV95")
  private Double nLV95;

  @JsonProperty("E_WGS84")
  private Double eWGS84;

  @JsonProperty("N_WGS84")
  private Double nWGS84;

  @JsonProperty("E_WGS84WEB")
  private Double eWGS84WEB;

  @JsonProperty("N_WGS84WEB")
  private Double nWGS84WEB;

  @JsonProperty("HTYP_ID")
  private Integer hTypId;

  @JsonProperty("BAV_BEMERKUNG")
  private String comment;
}
