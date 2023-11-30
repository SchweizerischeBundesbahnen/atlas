package ch.sbb.atlas.imports.prm.stoppoint;

import ch.sbb.atlas.imports.prm.BasePrmCsvModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StopPointCsvModel extends BasePrmCsvModel {

  @JsonProperty("DIDOK_CODE")
  private Integer didokCode;

  @JsonProperty("DS_SLOID")
  private String sloid;

  @JsonProperty("ADDRESS")
  private String address;

  @JsonProperty("ZIP_CODE")
  private String zipCode;

  @JsonProperty("CITY")
  private String city;

  @JsonProperty("ALTERNATIVE_TRANSPORT")
  private Integer alternativeTransport;

  @JsonProperty("ALT_TRANSPORT_CONDITION")
  private String alternativeTransportCondition;

  @JsonProperty("ASSISTANCE_AVAILABILITY")
  private Integer assistanceAvailability;

  @JsonProperty("ASSISTANCE_CONDITION")
  private String assistanceCondition;

  @JsonProperty("ASSISTANCE_SERVICE")
  private Integer assistanceService;

  @JsonProperty("AUDIO_TICK_MACH")
  private Integer audioTickMach;

  @JsonProperty("COMP_INFOS")
  private String compInfos;

  @JsonProperty("DYNAMIC_AUDIO_SYS")
  private Integer dynamicAudioSys;

  @JsonProperty("DYNAMIC_OPTIC_SYS")
  private Integer dynamicOpticSys;

  @JsonProperty("FREE_TEXT")
  private String freeText;

  @JsonProperty("INFO_TICK_MACH")
  private String infoTickMach;

  @JsonProperty("INTEROPERABLE")
  private Integer interoperable;

  @JsonProperty("ASSISTANCE_REQS_FULFILLED")
  private Integer assistanceReqsFulfilled;

  @JsonProperty("STATUS")
  private Integer status;

  @JsonProperty("TICKET_MACHINE")
  private Integer ticketMachine;

  @JsonProperty("URL")
  private String url;

  @JsonProperty("TRANSPORTATION_MEANS")
  private String transportationMeans;

  @JsonProperty("VISUAL_INFOS")
  private Integer visualInfos;

  @JsonProperty("WHEELCHAIR_TICK_MACH")
  private Integer wheelchairTickMach;

  public String getTransportationMeans() {
    return StopPointUtil.sortTransportationMeans(this.transportationMeans);
  }

}
