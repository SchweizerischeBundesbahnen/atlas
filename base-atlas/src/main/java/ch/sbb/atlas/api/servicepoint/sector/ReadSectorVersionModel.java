package ch.sbb.atlas.api.servicepoint.sector;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseReadModel;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "ReadSectorVersion")
public class ReadSectorVersionModel extends SectorVersionModel {

  private GeolocationBaseReadModel sectorGeolocation;

}
