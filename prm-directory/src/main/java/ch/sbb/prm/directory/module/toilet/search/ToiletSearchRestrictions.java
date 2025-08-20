package ch.sbb.prm.directory.module.toilet.search;

import ch.sbb.prm.directory.search.BasePrmSearchRestrictions;
import ch.sbb.prm.directory.module.toilet.entity.ToiletVersion;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
public class ToiletSearchRestrictions extends BasePrmSearchRestrictions<ToiletVersion> {

}
