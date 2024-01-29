package ch.sbb.atlas.api.prm.model.platform;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.prm.model.BasePrmVersionModel;
import ch.sbb.atlas.api.prm.model.PrmApiConstants;
import ch.sbb.atlas.validation.DatesValidator;
import ch.sbb.atlas.versioning.model.Versionable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
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
public class PlatformVersionModel extends BasePrmVersionModel implements DatesValidator, Versionable {

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = PrmApiConstants.PARENT_SLOID_DESCRIPTION, example = "ch:1:sloid:18771")
  @NotNull
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
  @Digits(integer = 10, fraction = 3)
  private Double height;

  @Schema(description = "Cross-platform slope [%]")
  @Digits(integer = 10, fraction = 3)
  private Double inclination;

  @Schema(description = "Longitudinal inclination of the holding edge [%]")
  @Digits(integer = 10, fraction = 3)
  private Double inclinationLongitudinal;

  @Schema(description = "Platform longitudinal inclination [%]")
  @Digits(integer = 10, fraction = 3)
  private Double inclinationWidth;

  @Schema(description = "Information options")
  private List<InfoOpportunityAttributeType> infoOpportunities;

  private BasicAttributeType levelAccessWheelchair;

  private Boolean partialElevation;

  @Schema(description = "Track superelevation value [mm]")
  @Digits(integer = 10, fraction = 3)
  private Double superelevation;

  private BooleanOptionalAttributeType tactileSystem;

  private VehicleAccessAttributeType vehicleAccess;

  @Schema(description = "Wheelchair Area Length [mm]")
  @Digits(integer = 10, fraction = 3)
  private Double wheelchairAreaLength;

  @Schema(description = "Wheelchair Area Width [mm]")
  @Digits(integer = 10, fraction = 3)
  private Double wheelchairAreaWidth;

}
