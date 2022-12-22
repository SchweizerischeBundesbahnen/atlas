package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import ch.sbb.atlas.servicepointdirectory.service.deserializer.LocalDateDeserializer;
import ch.sbb.atlas.servicepointdirectory.service.deserializer.LocalDateTimeDeserializer;
import ch.sbb.atlas.servicepointdirectory.service.deserializer.NumericBooleanDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDate;
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
public class LoadingPointCsvModel {

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
