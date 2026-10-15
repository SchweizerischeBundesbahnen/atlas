package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
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
@Schema(name = "UpdateLineVersionV3")
public class UpdateLineVersionModelV3 extends BaseLineVersionModelV3 {

  public static final String IMMUTABLE_MESSAGE = "is immutable and must not be sent on update";

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)", example = "5")
  @NotNull
  private Integer etagVersion;

  @Schema(hidden = true)
  @Null(message = "lineType " + IMMUTABLE_MESSAGE)
  private LineType lineType;

  @Schema(hidden = true)
  @Null(message = "referencedLineSlnid " + IMMUTABLE_MESSAGE)
  private String referencedLineSlnid;

}
