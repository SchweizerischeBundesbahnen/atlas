package ch.sbb.atlas.imports.prm;

import ch.sbb.atlas.imports.servicepoint.deserializer.LocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BasePrmCsvModel {

  @EqualsAndHashCode.Exclude
  @JsonProperty("VALID_FROM")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validFrom;

  @EqualsAndHashCode.Exclude
  @JsonProperty("VALID_TO")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validTo;

  // Create/Edit Info
  @EqualsAndHashCode.Exclude
  @JsonProperty("ADD_DATE")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  @EqualsAndHashCode.Exclude
  @JsonProperty("MODIFIED_DATE")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime modifiedAt;


}
