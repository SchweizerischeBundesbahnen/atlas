package ch.sbb.atlas.api.servicepoint.sector;

import ch.sbb.atlas.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Schema(name = "ReadSectorGroupVersion")
public class ReadSectorGroupVersionModel extends BaseSectorModel {

  @Schema(description = "Status", accessMode = AccessMode.READ_ONLY)
  private Status status;
}
