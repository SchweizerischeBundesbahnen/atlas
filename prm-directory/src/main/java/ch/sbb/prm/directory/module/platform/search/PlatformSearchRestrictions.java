package ch.sbb.prm.directory.module.platform.search;

import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.search.BasePrmSearchRestrictions;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
public class PlatformSearchRestrictions extends BasePrmSearchRestrictions<PlatformVersion> {

}
