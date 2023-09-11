package ch.sbb.atlas.imports.servicepoint.trafficpoint;

import ch.sbb.atlas.imports.servicepoint.BaseDidokCsvModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrafficPointElementCsvModel extends BaseDidokCsvModel {

  @JsonProperty("SLOID")
  private String sloid;

  @JsonProperty("DS_LAENDERCODE")
  private int country;

  @JsonProperty("DIDOK_CODE")
  private int servicePointNumber;

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
  private int trafficPointElementType;

  public int getServicePointNumber(){
    return ServicePointNumber.removeCheckDigit(servicePointNumber);
  }


}
