package ch.sbb.atlas.imports.servicepoint.loadingpoint;

import ch.sbb.atlas.imports.servicepoint.BaseDidokCsvModel;
import ch.sbb.atlas.imports.servicepoint.deserializer.NumericBooleanDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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

}
