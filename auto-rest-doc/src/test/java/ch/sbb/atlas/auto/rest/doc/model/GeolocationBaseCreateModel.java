package ch.sbb.atlas.auto.rest.doc.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
@Schema(name = "GeolocationCreate")
public class GeolocationBaseCreateModel {

  @NotNull
  @Schema(description = "North longitude", example = "225738.00000000000")
  private Double north;

  @NotNull
  @Schema(description = "Eastern longitude", example = "681821.00000000000")
  private Double east;

  @Schema(description = "Height of the coordinate point", example = "540.20000")
  @Digits(integer = 5, fraction = 4)
  private Double height;

}
