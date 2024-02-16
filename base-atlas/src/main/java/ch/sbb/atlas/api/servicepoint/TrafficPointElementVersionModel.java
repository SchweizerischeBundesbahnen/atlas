package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.atlas.validation.DatesValidator;
import ch.sbb.atlas.versioning.model.Versionable;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
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
public abstract class TrafficPointElementVersionModel extends BaseVersionModel implements DatesValidator, Versionable {

  @Schema(description = "Technical identifier", accessMode = AccessMode.READ_ONLY, example = "1")
  private Long id;

  @Schema(description = "Designation used in the customer information systems.", example = "Bezeichnung")
  @Size(max = AtlasFieldLengths.LENGTH_40)
  private String designation;

  @Schema(description = "Designation used in (operational) timetable planning.", example = "Bezeichnung")
  @Size(max = AtlasFieldLengths.LENGTH_20)
  private String designationOperational;

  @Schema(description = "Length of the TrafficPointElement", example = "18.000")
  @Digits(integer = 13, fraction = 3)
  @Min(0)
  private Double length;

  @Schema(description = "Height of BoardingArea at the TrafficPointElement", example = "22.00")
  @Digits(integer = 5, fraction = 2)
  @Min(0)
  private Double boardingAreaHeight;

  @Schema(description = "Compass Direction at the TrafficPointElement", example = "107.00")
  @Digits(integer = 5, fraction = 2)
  @Min(0)
  @Max(360)
  private Double compassDirection;

  @NotNull
  private TrafficPointElementType trafficPointElementType;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_128)
  @Schema(description = "Unique code for traffic point element (TPE) that is used in customer information.\n" +
      "By means of this ID, the connection between stops and bus / station stop area or boarding area can be established.\n\n" +
      "The structure is described in the “Swiss Location ID” specification, chapter 4.2. The document is available here.\n\n" +
      "https://transportdatamanagement.ch/standards/", example = "ch:1:sloid:16161:1")
  private String sloid;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_128)
  @Schema(description = "Hierarchical assignment of the TPE which is to be processed to another TPE. It is a 1:1 relationship. "
      + "As key, the SLOID is used.", example = "ch:1:sloid:16161:1")
  private String parentSloid;

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)", example = "5")
  private Integer etagVersion;

}
