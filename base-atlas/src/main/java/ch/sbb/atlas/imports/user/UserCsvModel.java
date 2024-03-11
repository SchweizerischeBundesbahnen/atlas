package ch.sbb.atlas.imports.user;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCsvModel {

  @JsonProperty("userid")
  private String userid;

  @JsonProperty("countries")
  private String countries;

  @JsonProperty("sboids")
  private String sboids;

  @JsonProperty("role")
  private String role;

  private ApplicationType applicationType;

}
