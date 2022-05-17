package ch.sbb.line.directory.model;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.searching.SearchRestrictions;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumber_;
import javax.persistence.metamodel.SingularAttribute;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
public class TimetableFieldNumberSearchRestrictions extends
    SearchRestrictions<TimetableFieldNumber> {

  @Override
  protected SingularAttribute<TimetableFieldNumber, Status> getStatus() {
    return TimetableFieldNumber_.status;
  }

}
