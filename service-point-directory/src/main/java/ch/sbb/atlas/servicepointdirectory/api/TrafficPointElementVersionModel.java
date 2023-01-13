package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.api.BaseVersionModel;
import ch.sbb.atlas.base.service.model.validation.DatesValidator;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.servicepointdirectory.converter.ServicePointNumberConverter;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.TrafficPointElementGeolocation;
import ch.sbb.atlas.servicepointdirectory.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDate;
import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@Schema(name = "TrafficPointElementVersion")
public class TrafficPointElementVersionModel extends BaseVersionModel implements DatesValidator {

  @Schema(description = "Technical identifier", accessMode = AccessMode.READ_ONLY, example = "1")
  private Long id;

  @Schema(description = "Designation used in the customer information systems.", example = "1")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  private String designation;

  @Schema(description = "Designation used in (operational) timetable planning.", example = "2")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  private String designationOperational;

  @Schema(description = "Length of the TrafficPointElement", example = "170")
  private Double length;

  @Schema(description = "Height of BoardingArea at the TrafficPointElement", example = "15")
  private Double boardingAreaHeight;

  @Schema(description = "Compass Direction at the TrafficPointElement", example = "43")
  private Double compassDirection;

  private TrafficPointElementType trafficPointElementType;

  @NotNull
  @Valid
  private ServicePointNumber servicePointNumber;

  @NotNull
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = "Unique code for traffic point element (TPE) that is used in customer information.\n" +
      "By means of this ID, the connection between stops and bus / station stop area or boarding area can be established.\n\n" +
      "The structure is described in the “Swiss Location ID” specification, chapter 4.2. The document is available here.\n\n" +
      "https://transportdatamanagement.ch/standards/", example = "ch:1:sloid:16161:1")
  private String sloid;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = "Hierarchical assignment of the TPE which is to be processed to another TPE. It is a 1:1 relationship. "
      + "As key, the SLOID is used.", example = "ch:1:sloid:16161:1")
  private String parentSloid;

  private GeolocationModel trafficPointElementGeolocation;

  @JsonInclude
  @Schema(description = "TrafficPointElementVersion has a Geolocation")
  public boolean isHasGeolocation() {
    return trafficPointElementGeolocation != null;
  }

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

  public static TrafficPointElementVersionModel fromEntity(TrafficPointElementVersion trafficPointElementVersion) {
    return TrafficPointElementVersionModel.builder()
        .id(trafficPointElementVersion.getId())
        .sloid(trafficPointElementVersion.getSloid())
        .designation(trafficPointElementVersion.getDesignation())
        .designationOperational(trafficPointElementVersion.getDesignationOperational())
        .length(trafficPointElementVersion.getLength())
        .boardingAreaHeight(trafficPointElementVersion.getBoardingAreaHeight())
        .compassDirection(trafficPointElementVersion.getCompassDirection())
        .trafficPointElementType(trafficPointElementVersion.getTrafficPointElementType())
        .servicePointNumber(trafficPointElementVersion.getServicePointNumber())
        .parentSloid(trafficPointElementVersion.getParentSloid())
        .validFrom(trafficPointElementVersion.getValidFrom())
        .validTo(trafficPointElementVersion.getValidTo())
        .trafficPointElementGeolocation(GeolocationModel.fromEntity(trafficPointElementVersion.getTrafficPointElementGeolocation()))
        .creationDate(trafficPointElementVersion.getCreationDate())
        .creator(trafficPointElementVersion.getCreator())
        .editionDate(trafficPointElementVersion.getEditionDate())
        .editor(trafficPointElementVersion.getEditor())
        .build();
  }

}
