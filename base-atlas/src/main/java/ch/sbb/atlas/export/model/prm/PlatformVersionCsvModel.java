package ch.sbb.atlas.export.model.prm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@FieldNameConstants
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlatformVersionCsvModel {

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

    private String vehicleAccess;

    private Double wheelChairAreaLength;

    private Double wheelChairAreaWidth;

    private String validFrom;

    private String validTo;

    private String creationDate;

    private String editionDate;
}
