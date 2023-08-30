package ch.sbb.atlas.imports.servicepoint.loadingpoint;

import ch.sbb.atlas.imports.servicepoint.BaseDidokCsvModel;
import ch.sbb.atlas.imports.servicepoint.deserializer.NumericBooleanDeserializer;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoadingPointCsvModel extends BaseDidokCsvModel {

  @JsonProperty("LADESTELLEN_NUMMER")
  private Integer number;

  @JsonProperty("BEZEICHNUNG")
  private String designation;

  @JsonProperty("BEZEICHNUNG_LANG")
  private String designationLong;

  @JsonProperty("IS_ANSCHLUSSPUNKT")
  @JsonDeserialize(using = NumericBooleanDeserializer.class)
  private Boolean connectionPoint;

  @JsonProperty("DIDOK_CODE")
  private Integer servicePointNumber;

  public Integer getServicePointNumber(){
    return ServicePointNumber.removeCheckDigit(servicePointNumber);
  }

}
