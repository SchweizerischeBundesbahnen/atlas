package ch.sbb.prm.directory.module.parkinglot.search;

import ch.sbb.prm.directory.module.parkinglot.entity.ParkingLotVersion;
import ch.sbb.prm.directory.search.BasePrmSearchRestrictions;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
public class ParkingLotSearchRestrictions extends BasePrmSearchRestrictions<ParkingLotVersion> {

}
