package ch.sbb.line.directory.model;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementRequestParams;
import ch.sbb.atlas.searching.specification.EnumSpecification;
import ch.sbb.atlas.searching.specification.LongSpecification;
import ch.sbb.atlas.searching.specification.SearchCriteriaSpecification;
import ch.sbb.atlas.searching.specification.SingleStringSpecification;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.entity.TimetableHearingStatement.Fields;
import ch.sbb.line.directory.entity.TimetableHearingStatement_;
import java.util.List;
import java.util.Optional;
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
        .and(new EnumSpecification<>(statementRequestParams.getStatusRestrictions(), TimetableHearingStatement_.statementStatus))
        .and(new SingleStringSpecification<>(Optional.ofNullable(statementRequestParams.getTtfnid()), Fields.ttfnid))
        .and(new ResponsibleTransportCompanySpecification(statementRequestParams.getTransportCompanies()))
        .and(new SearchCriteriaSpecification<>(statementRequestParams.getSearchCriterias(),
            List.of(Fields.id, Fields.statement, Fields.justification, Fields.stopPlace,
                "statementSender.firstName", "statementSender.lastName",
                "statementSender.organisation")));
  }

}
