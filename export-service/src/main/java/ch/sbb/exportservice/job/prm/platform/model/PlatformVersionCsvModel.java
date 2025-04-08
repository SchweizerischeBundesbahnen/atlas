package ch.sbb.exportservice.job.prm.platform.model;

import ch.sbb.exportservice.job.prm.BasePrmCsvModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@SuperBuilder
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlatformVersionCsvModel extends BasePrmCsvModel {

  private String sloid;

  private String parentSloidServicePoint;

  private Integer parentNumberServicePoint;

  private String boardingDevice;

  private String adviceAccessInfo;

  private String additionalInformation;

  private String contrastingAreas;

  private String dynamicAudio;

  private String dynamicVisual;

  private Double height;

  private Double inclination;

  private Double inclinationLongitudinal;

  private Double inclinationWidth;

  private String infoOpportunities;

  private String levelAccessWheelchair;

  private Boolean partialElevation;

  private Double superElevation;

  private String tactileSystems;

  private String boardingMark;

  private String vehicleAccess;

  private Double wheelChairAreaLength;

  private Double wheelChairAreaWidth;

}
