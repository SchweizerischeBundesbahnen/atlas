package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.model.Status;
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
@Schema(name = "CreateLineVersionV3")
public class CreateLineVersionModelV3 extends BaseLineVersionModelV3 {

  public static final String READ_ONLY_MESSAGE = "must not be set on create - it is assigned by the server";

  @Schema(description = "LineType - for a subline it must match the type of the referenced line")
  @NotNull
  private LineType lineType;

  @Schema(description = "SLNID of the parent line. Present: a subline is created. Absent: a line is created.",
      example = "ch:1:slnid:1028629")
  private String referencedLineSlnid;

  @Schema(hidden = true)
  @Null(message = "id " + READ_ONLY_MESSAGE)
  private Long id;

  @Schema(hidden = true)
  @Null(message = "slnid " + READ_ONLY_MESSAGE)
  private String slnid;

  @Schema(hidden = true)
  @Null(message = "status " + READ_ONLY_MESSAGE)
  private Status status;

  @Schema(hidden = true)
  @Null(message = "etagVersion " + READ_ONLY_MESSAGE)
  private Integer etagVersion;

}
