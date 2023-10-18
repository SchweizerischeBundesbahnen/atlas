package ch.sbb.atlas.api.prm.model.platform;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.prm.model.BasePrmVersionModel;
import ch.sbb.atlas.validation.DatesValidator;
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
public abstract class PlatformVersionModel extends BasePrmVersionModel implements DatesValidator {

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = "Parent Service Point Sloid: ServiceUnique code for locations that is used in customer information. The "
      + "structure is described in the “Swiss Location ID” specification, chapter 4.2. The document is available here. "
      + "https://transportdatamanagement.ch/standards/", example = "ch:1:sloid:18771")
  private String parentServicePointSloid;

  @Schema(description = "Wheelchair aids")
  private BoardingDeviceAttributeType boardingDevice;

  @Schema(description = "Information on access to transport")
  private String adviceAccessInfo;

  @Schema(description = "Additional Information")
  private String additionalInformation;

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
