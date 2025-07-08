package ch.sbb.atlas.api.servicepoint.sector;

import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public abstract class BaseSectorCoreModel extends BaseSectorVersionModel {

  @Schema(description = """
      This ID helps identify versions of a traffic point element in the use case front end and/or update.
      This ID can be deleted if the version is no longer present. Do not use this ID to map your object to a traffic point element.
      To do this, use the sloid in combination with the data range (valid from/valid until).
      """,
      accessMode = AccessMode.READ_ONLY, example = "1")
  private Long id;

  @Schema(description = "Unique code for sector that is used in customer information.\n" +
      "https://transportdatamanagement.ch/standards/", example = "ch:1:sloid:16161:1")
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_128)
  @NotNull
  private String sloid;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_128)
  @Schema(description = """
      Unique code for traffic point element (TPE) that is used in customer information.
      By means of this ID, the connection between stops and bus / station stop area or boarding area can be established.
      The structure is described in the “Swiss Location ID” specification, chapter 4.2. The document is available here.
      https://transportdatamanagement.ch/standards/
      """,
      example = "ch:1:sloid:16161:1")
  @NotNull
  private String trafficPointSloid;

}
