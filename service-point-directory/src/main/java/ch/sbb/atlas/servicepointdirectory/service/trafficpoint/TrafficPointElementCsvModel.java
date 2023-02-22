package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.imports.servicepoint.BaseDidokCsvModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrafficPointElementCsvModel extends BaseDidokCsvModel {

  @JsonProperty("SLOID")
  private String sloid;

  @JsonProperty("DS_LAENDERCODE")
  private Integer country;

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

}
