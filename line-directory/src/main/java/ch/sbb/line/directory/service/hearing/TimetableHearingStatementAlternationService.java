package ch.sbb.line.directory.service.hearing;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementAlternatingModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementRequestParams;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.mapper.TimetableHearingStatementMapperV2;
import ch.sbb.line.directory.model.TimetableHearingStatementSearchRestrictions;
import ch.sbb.line.directory.repository.TimetableHearingStatementRepository;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TimetableHearingStatementAlternationService {

  private static final IntUnaryOperator NEXT = i -> i + 1;
  private static final IntUnaryOperator PREVIOUS = i -> i - 1;

  private final TimetableHearingStatementRepository timetableHearingStatementRepository;

  public TimetableHearingStatementAlternatingModel getPreviousStatement(Long id, Pageable pageable,
      TimetableHearingStatementRequestParams statementRequestParams) {
    return getStatementAlternation(id, pageable, statementRequestParams, PREVIOUS);
  }

  public TimetableHearingStatementAlternatingModel getNextStatement(Long id, Pageable pageable,
      TimetableHearingStatementRequestParams statementRequestParams) {
    return getStatementAlternation(id, pageable, statementRequestParams, NEXT);
  }

  private TimetableHearingStatementAlternatingModel getStatementAlternation(
      Long id, Pageable pageable, TimetableHearingStatementRequestParams statementRequestParams,
      IntUnaryOperator indexModifier
  ) {
    List<TimetableHearingStatement> hearingStatements = getAllHearingStatements(
        TimetableHearingStatementSearchRestrictions.builder()
            .pageable(pageable)
            .statementRequestParams(statementRequestParams).build());

    OptionalInt lookupCurrentStatementIndex = IntStream.range(0, hearingStatements.size())
        .filter(i -> hearingStatements.get(i).getId().equals(id))
        .findFirst();
    if (lookupCurrentStatementIndex.isEmpty()) {
      return TimetableHearingStatementAlternatingModel.builder()
          .pageable(pageable)
          .timetableHearingStatement(getStatementNotInTableFallback(id, hearingStatements))
          .build();
    }

    int indexOfCurrentStatement = lookupCurrentStatementIndex.orElseThrow();

    int indexOfAlternation = indexModifier.applyAsInt(indexOfCurrentStatement);
    if (indexOfAlternation < 0) {
      indexOfAlternation += hearingStatements.size();
    }
    indexOfAlternation %= hearingStatements.size();
    TimetableHearingStatement alternation = hearingStatements.get(indexOfAlternation);

    Pageable resultPageable = PageRequest.of((indexOfAlternation) / pageable.getPageSize(), pageable.getPageSize(),
        pageable.getSort());

    return TimetableHearingStatementAlternatingModel.builder()
        .timetableHearingStatement(TimetableHearingStatementMapperV2.toModel(alternation))
        .pageable(resultPageable)
        .build();
  }

  private List<TimetableHearingStatement> getAllHearingStatements(
      TimetableHearingStatementSearchRestrictions searchRestrictions) {
    return timetableHearingStatementRepository.findAll(searchRestrictions.getSpecification(),
        searchRestrictions.getPageable().getSort());
  }

  private TimetableHearingStatementModelV2 getStatementNotInTableFallback(Long id,
      List<TimetableHearingStatement> hearingStatements) {
    return TimetableHearingStatementMapperV2.toModel(
        hearingStatements.stream().filter(i -> i.getId() >= id).findFirst().orElse(hearingStatements.getLast()));
  }
}
