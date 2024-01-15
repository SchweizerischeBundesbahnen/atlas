package ch.sbb.exportservice.entity;

import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
public class PlatformVersion extends BaseEntity {

    private Long id;

    private String sloid;

    private String parentSloidServicePoint;

    private Integer parentNumberServicePoint;

    private BoardingDeviceAttributeType boardingDevice;

    private String adviceAccessInfo;

    private String additionalInformation;

    private BooleanOptionalAttributeType contrastingAreas;

    private BasicAttributeType dynamicAudio;

    private BasicAttributeType dynamicVisual;

    private Double height;

    private Double inclination;

    private Double inclinationLongitudinal;

    private Double inclinationWidth;

    private Set<InfoOpportunityAttributeType> infoOpportunities;

    private String infoOpportunitiesPipeList;

    private BasicAttributeType levelAccessWheelchair;

    private Boolean partialElevation;

    private Double superElevation;

    private BooleanOptionalAttributeType tactileSystems;

    private VehicleAccessAttributeType vehicleAccess;

    private Double wheelchairAreaLength;

    private Double wheelchairAreaWidth;

    private LocalDate validTo;

    private LocalDate validFrom;

    @ToString.Include
    public Boolean isPartialElevation() {
        return partialElevation != null;
    }

}
