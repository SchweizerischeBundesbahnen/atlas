package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.BaseVersionModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
@Schema(name = "ServicePointFotComment")
public class ServicePointFotCommentModel extends BaseVersionModel {

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_2000)
  @Schema(description = "FotComment", example = "Good Service Point.")
  private String fotComment;

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)", example = "5")
  private Integer etagVersion;

}