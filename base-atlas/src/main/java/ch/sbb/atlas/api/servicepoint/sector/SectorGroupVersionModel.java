package ch.sbb.atlas.api.servicepoint.sector;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Schema(name = "SectorGroupVersionModel")
public class SectorGroupVersionModel extends AbstractSectorCore {

}
