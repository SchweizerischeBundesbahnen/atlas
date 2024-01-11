package ch.sbb.atlas.export.model.prm;

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

    private Double inclinationLongitudal;

    private Double inclinationWidth;

    private String infoOpportunities;

    private String levelAccessWheelchair;

    private Boolean partialElevation;

    private Double superElevation;

    private String tactileSystems;

    private String vehicleAccess;

    private Double wheelchairAreaLength;

    private Double wheelChairAreaWidth;

    private String validFrom;

    private String validTo;

    private String creationDate;

    private String creator;

    private String editionDate;

    private String editor;
}
