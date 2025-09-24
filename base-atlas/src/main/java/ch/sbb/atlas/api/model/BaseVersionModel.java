package ch.sbb.atlas.api.model;

import ch.sbb.atlas.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
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
public abstract class BaseVersionModel extends AuditableVersionModel {

  @Schema(description = "Status", accessMode = AccessMode.READ_ONLY)
  private Status status;

}
