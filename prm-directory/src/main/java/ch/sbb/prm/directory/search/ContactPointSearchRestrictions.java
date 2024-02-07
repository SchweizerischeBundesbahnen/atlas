package ch.sbb.prm.directory.search;

import ch.sbb.prm.directory.entity.ContactPointVersion;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
public class ContactPointSearchRestrictions extends BasePrmSearchRestrictions<ContactPointVersion> {
}
