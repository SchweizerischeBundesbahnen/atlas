package ch.sbb.atlas.servicepointdirectory.service.traffic.point;

import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.service.LocalDateDeserializer;
import ch.sbb.atlas.servicepointdirectory.service.LocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrafficPointElementCsvModel {

  @JsonProperty("SLOID")
  private String sloid;

  @JsonProperty("DIDOK_CODE")
  private Integer servicePointNumber;

  @JsonProperty("BEZEICHNUNG")
  private String designation;

  @JsonProperty("BEZEICHNUNG_BETRIEBLICH")
  private String designationOperational;

  @JsonProperty("LAENGE")
  private Double length;

  @JsonProperty("KANTENHOEHE")
  private Double boardingAreaHeight;

  @JsonProperty("KOMPASSRICHTUNG")
  private Double compassDirection;

  @JsonProperty("BPVE_ID")
  private String parentSloid;

  @JsonProperty("BPVE_TYPE")
  private Integer trafficPointElementType;

  @JsonProperty("SOURCE_SPATIAL_REF")
  private SpatialReference spatialReference;

  @JsonProperty("E_LV95")
  private Double eLv95;
  @JsonProperty("N_LV95")
  private Double nLv95;

  @JsonProperty("E_LV03")
  private Double eLv03;
  @JsonProperty("N_LV03")
  private Double nLv03;

  @JsonProperty("E_WGS84")
  private Double eWgs84;
  @JsonProperty("N_WGS84")
  private Double nWgs84;

  @JsonProperty("Z_LV95")
  private Double height;

  @JsonProperty("GUELTIG_VON")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validFrom;

  @JsonProperty("GUELTIG_BIS")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validTo;

  @JsonProperty("ERSTELLT_AM")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  @JsonProperty("ERSTELLT_VON")
  private String createdBy;

  @JsonProperty("GEAENDERT_VON")
  private String editedBy;

  @JsonProperty("GEAENDERT_AM")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime editedAt;

}
