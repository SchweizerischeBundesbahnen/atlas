package ch.sbb.prm.directory.domain.toilet.search;

import ch.sbb.prm.directory.search.BasePrmSearchRestrictions;
import ch.sbb.prm.directory.domain.toilet.entity.ToiletVersion;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
public class ToiletSearchRestrictions extends BasePrmSearchRestrictions<ToiletVersion> {

}
