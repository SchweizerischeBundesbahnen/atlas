package ch.sbb.prm.directory.search;

import ch.sbb.prm.directory.entity.PlatformVersion;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
public class PlatformSearchRestrictions extends BasePrmSearchRestrictions<PlatformVersion> {

}
