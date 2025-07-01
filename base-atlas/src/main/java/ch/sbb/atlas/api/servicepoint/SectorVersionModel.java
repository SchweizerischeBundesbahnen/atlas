package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.BaseVersionModel;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.Digits;
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
@Schema(name = "SectorVersionModel")
public class SectorVersionModel extends BaseVersionModel {

  @Schema(description = """
      This ID helps identify versions of a traffic point element in the use case front end and/or update.
      This ID can be deleted if the version is no longer present. Do not use this ID to map your object to a traffic point element.
      To do this, use the sloid in combination with the data range (valid from/valid until).
      """,
      accessMode = AccessMode.READ_ONLY, example = "1")
  private Long id;

  //TODO remove after ATLAS-2963 is implemented. SLOID must be generated and cannot be set manually.
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_128)
  @Schema(description = "Unique code for sctor that is used in customer information.\n" +
      "https://transportdatamanagement.ch/standards/", example = "ch:1:sloid:16161:1")
  private String sloid;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_128)
  @Schema(description = "Unique code for traffic point element (TPE) that is used in customer information.\n" +
      "By means of this ID, the connection between stops and bus / station stop area or boarding area can be established.\n\n" +
      "The structure is described in the “Swiss Location ID” specification, chapter 4.2. The document is available here.\n\n" +
      "https://transportdatamanagement.ch/standards/", example = "ch:1:sloid:16161:1")
  private String trafficPointSloid;

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

  @Schema(description = "Designation used in the customer information systems.", example = "Bezeichnung")
  @Size(max = AtlasFieldLengths.LENGTH_40)
  private String designation;

  @NotNull
  @Digits(integer = 8, fraction = 11)
  @Schema(description = "North longitude", example = "225738.00000000000")
  private Double north;

  @NotNull
  @Digits(integer = 8, fraction = 11)
  @Schema(description = "Eastern longitude", example = "681821.00000000000")
  private Double east;

  @Schema(description = "Height of the coordinate point", example = "540.20000")
  @Digits(integer = 5, fraction = 4)
  private Double height;

  @Schema(description = "Coordinate system spatial reference", example = "LV95")
  @NotNull
  private SpatialReference spatialReference;

  @Schema(description = "", example = "18.000")
  @Digits(integer = 13, fraction = 3)
  @Min(0)
  private Double length;

  @Schema(description = "", example = "18.000")
  @Digits(integer = 13, fraction = 3)
  @Min(0)
  private Double edgeHeight;

}
