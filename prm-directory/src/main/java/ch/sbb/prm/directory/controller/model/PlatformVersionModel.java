package ch.sbb.prm.directory.controller.model;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.validation.DatesValidator;
import ch.sbb.prm.directory.enumeration.BasicAttributeType;
import ch.sbb.prm.directory.enumeration.BoardingDeviceAttributeType;
import ch.sbb.prm.directory.enumeration.BooleanAttributeType;
import ch.sbb.prm.directory.enumeration.BooleanOptionalAttributeType;
import ch.sbb.prm.directory.enumeration.InfoOpportunityAttributeType;
import ch.sbb.prm.directory.enumeration.VehicleAccessAttributeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "PlatformVersion")
public class PlatformVersionModel extends BasePrmVersionModel implements DatesValidator {

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = "Hierarchical assignment of the TPE which is to be processed to another TPE. It is a 1:1 relationship. "
      + "As key, the SLOID is used.", example = "ch:1:sloid:16161:1")
  private String parentServicePointSloid;

  @Schema(description = "Wheelchair aids")
  private BoardingDeviceAttributeType boardingDevice;

  @Schema(description = "Information on access to transport")
  private String adviceAccessInfo;

  @Schema(description = "Notes on access to the holding edge")
  private String additionalInfo;

  @Schema(description = "Tactile-visual marking of platform surfaces")
  private BooleanOptionalAttributeType contrastingAreas;

  @Schema(description = "Acoustic information")
  private BasicAttributeType dynamicAudio;

  @Schema(description = "Dynamic optical information")
  private BasicAttributeType dynamicVisual;

  @Schema(description = "Height [cm]")
  private Double height;

  @Schema(description = "Cross-platform slope [%]")
  private Double inclination;

  @Schema(description = "Longitudinal inclination of the holding edge [%]")
  private Double inclinationLongitudinal;

  @Schema(description = "Platform longitudinal inclination [%]")
  private Double inclinationWidth;

  @Schema(description = "Information options")
  private List<InfoOpportunityAttributeType> infoOpportunities;

  @Schema(description = "Level access")
  private BasicAttributeType levelAccessWheelchair;

  @Schema(description = "Partial elevation")
  private BooleanAttributeType partialElevation;

  @Schema(description = "Track superelevation value [mm]")
  private Double superelevation;

  @Schema(description = "Tactile guidance system")
  private BooleanOptionalAttributeType tactileSystem;

  @Schema(description = "Access to the platform. Getting into the vehicle")
  private VehicleAccessAttributeType vehicleAccess;

  @Schema(description = "Wheelchair Area Length [mm]")
  private Double wheelchairAreaLength;

  @Schema(description = "Wheelchair Area Width [mm]")
  private Double wheelchairAreaWidth;

}
