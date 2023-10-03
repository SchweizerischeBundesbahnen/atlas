package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.validation.DatesValidator;
import ch.sbb.prm.directory.enumeration.BasicAttributeType;
import ch.sbb.prm.directory.enumeration.BoardingDeviceType;
import ch.sbb.prm.directory.enumeration.BooleanAttributeType;
import ch.sbb.prm.directory.enumeration.BooleanOptionalAttributeType;
import ch.sbb.prm.directory.enumeration.InfoOpportunityType;
import ch.sbb.prm.directory.enumeration.VehicleAccessType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
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
@Schema(name = "LineVersion")
public class PlatformVersionModel extends BaseVersionModel implements DatesValidator {

  @Schema(description = "Technical identifier", accessMode = AccessMode.READ_ONLY, example = "1")
  private Long id;

  @NotNull
  @Valid
  private ServicePointNumber number;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = "Unique code for locations that is used in customer information. The structure is described in the "
      + "“Swiss Location ID” specification, chapter 4.2. The document is available here. "
      + "https://transportdatamanagement.ch/standards/", example = "ch:1:sloid:18771")
  private String sloid;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = "Hierarchical assignment of the TPE which is to be processed to another TPE. It is a 1:1 relationship. "
      + "As key, the SLOID is used.", example = "ch:1:sloid:16161:1")
  private String parentServicePointSloid;

  @Schema(description = "Valid from")
  @NotNull
  private LocalDate validFrom;

  @Schema(description = "Valid to")
  @NotNull
  private LocalDate validTo;

  @Schema(description = "Wheelchair aids")
  private BoardingDeviceType boardingDevice;

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
  private List<InfoOpportunityType> infoOpportunities;

  @Schema(description = "Level access")
  private BasicAttributeType levelAccessWheelchair;

  @Schema(description = "Partial elevation")
  private BooleanAttributeType partialElevation;

  @Schema(description = "Track superelevation value [mm]")
  private Double superelevation;

  @Schema(description = "Tactile guidance system")
  private BooleanOptionalAttributeType tactileSystem;

  @Schema(description = "Access to the platform. Getting into the vehicle")
  private VehicleAccessType vehicleAccess;

  @Schema(description = "Wheelchair Area Length [mm]")
  private Double wheelchairAreaLength;

  @Schema(description = "Wheelchair Area Width [mm]")
  private Double wheelchairAreaWidth;

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)", example = "5")
  private Integer etagVersion;

}
