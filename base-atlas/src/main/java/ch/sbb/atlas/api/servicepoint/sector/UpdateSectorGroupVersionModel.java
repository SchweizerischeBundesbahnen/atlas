package ch.sbb.atlas.api.servicepoint.sector;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Schema(name = "UpdateSectorGroupVersion")
public class UpdateSectorGroupVersionModel extends BaseSectorVersionModel {

}
