package ch.sbb.atlas.imports.prm.platform;

import ch.sbb.atlas.imports.prm.BasePrmCsvModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@ToString(callSuper = true)
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlatformCsvModel extends BasePrmCsvModel {

  @JsonProperty("SLOID")
  private String sloid;

  @JsonProperty("DIDOK_CODE")
  private Integer didokCode;

  @JsonProperty("STATUS")
  private Integer status;

  @JsonProperty("BOARDING_DEVICE")
  private Integer boardingDevice;

  @JsonProperty("COMPL_ACCESS_INFO")
  private String accessInfo;

  @JsonProperty("COMPL_INFOS")
  private String infos;

  @JsonProperty("CONTRASTING_AREAS")
  private Integer contrastingAreas;

  @JsonProperty("DYNAMIC_AUDIO")
  private Integer dynamicAudio;

  @JsonProperty("DYNAMIC_VISUAL")
  private Integer dynamicVisual;

  @JsonProperty("HEIGHT")
  private Double height;

  @JsonProperty("INCLINATION")
  private Double inclination;

  @JsonProperty("INCLINATION_LONG")
  private Double inclinationLong;

  @JsonProperty("INCLINATION_WIDTH")
  private Double inclinationWidth;

  @JsonProperty("INFO_BLINDS")
  private String infoBlinds;

  @JsonProperty("LEVEL_ACCESS_WHEELCHAIR")
  private Integer levelAccessWheelchair;

  @JsonProperty("PARTIAL_ELEV")
  private Integer partialElev;

  @JsonProperty("SUPERELEVATION")
  private Double superelevation;

  @JsonProperty("TACTILE_SYSTEMS")
  private Integer tactileSystems;

  @JsonProperty("VEHICLE_ACCESS")
  private Integer vehicleAccess;

  @JsonProperty("WHEELCHAIR_AREA_LENGTH")
  private Double wheelchairAreaLength;

  @JsonProperty("WHEELCHAIR_AREA_WIDTH")
  private Double wheelchairAreaWidth;

  @JsonProperty("VALID_FROM")
  private LocalDate validFrom;

  @JsonProperty("VALID_TO")
  private LocalDate validTo;

  @JsonProperty("ADD_DATE")
  private LocalDateTime addDate;

  @JsonProperty("ADDED_BY")
  private String addedBy;

  @JsonProperty("MODIFIED_DATE")
  private LocalDateTime modifiedDate;

  @JsonProperty("MODIFIED_BY")
  private String modifiedBy;

  @JsonProperty("DS_SLOID")
  private String dsSloid;

}
