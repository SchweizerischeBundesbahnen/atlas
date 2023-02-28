package ch.sbb.atlas.timetable.hearing.model;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementRequestParams;
import ch.sbb.atlas.searching.specification.EnumSpecification;
import ch.sbb.atlas.searching.specification.LongSpecification;
import ch.sbb.atlas.searching.specification.SearchCriteriaSpecification;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingStatement;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingStatement.Fields;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingStatement_;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class TimetableHearingStatementSearchRestrictions {

  private final Pageable pageable;

  private final TimetableHearingStatementRequestParams statementRequestParams;

  public Specification<TimetableHearingStatement> getSpecification() {
    return new LongSpecification<>(TimetableHearingStatement_.timetableYear, statementRequestParams.getTimetableHearingYear())
        .and(new EnumSpecification<>(statementRequestParams.getCanton(), TimetableHearingStatement_.swissCanton))
        .and(new SearchCriteriaSpecification<>(statementRequestParams.getSearchCriterias(),
            List.of(Fields.statement, Fields.justification)));
  }

}
