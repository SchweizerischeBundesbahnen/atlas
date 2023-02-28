package ch.sbb.atlas.timetable.hearing.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementApiV1;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementRequestParams;
import ch.sbb.atlas.timetable.hearing.entity.StatementDocument;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingStatement;
import ch.sbb.atlas.timetable.hearing.mapper.TimeTableHearingStatementMapper;
import ch.sbb.atlas.timetable.hearing.model.TimetableHearingStatementSearchRestrictions;
import ch.sbb.atlas.timetable.hearing.service.TimetableFieldNumberResolverService;
import ch.sbb.atlas.timetable.hearing.service.TimetableHearingStatementService;
import ch.sbb.atlas.timetable.hearing.service.TimetableHearingYearService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TimetableHearingStatementController implements TimetableHearingStatementApiV1 {

  private final TimetableHearingStatementService timetableHearingStatementService;
  private final TimetableHearingYearService timetableHearingYearService;
  private final TimetableFieldNumberResolverService timetableFieldNumberResolverService;

  @Override
  public Container<TimetableHearingStatementModel> getStatements(Pageable pageable,
      TimetableHearingStatementRequestParams statementRequestParams) {
    Page<TimetableHearingStatement> hearingStatements = timetableHearingStatementService.getHearingStatements(
        TimetableHearingStatementSearchRestrictions.builder()
            .pageable(pageable)
            .statementRequestParams(statementRequestParams).build());
    return Container.<TimetableHearingStatementModel>builder()
        .objects(hearingStatements.stream().map(TimeTableHearingStatementMapper::toModel).toList())
        .totalCount(hearingStatements.getTotalElements())
        .build();
  }

  public TimetableHearingStatementModel getStatement(Long id) {
    return TimeTableHearingStatementMapper.toModel(timetableHearingStatementService.getStatementById(id));
  }

  @Override
  public TimetableHearingStatementModel createStatement(TimetableHearingStatementModel statement,
      List<MultipartFile> documents) {
    TimetableHearingStatement statementToCreate = TimeTableHearingStatementMapper.toEntity(statement);
    addFilesToStatement(documents, statementToCreate);

    TimetableHearingStatement hearingStatement = timetableHearingStatementService.createHearingStatement(statementToCreate);
    return TimeTableHearingStatementMapper.toModel(hearingStatement);
  }

  @Override
  public TimetableHearingStatementModel createStatementExternal(TimetableHearingStatementModel statement,
      List<MultipartFile> documents) {
    String resolvedTtfnid =
        timetableFieldNumberResolverService.resolveTtfnid(statement.getTimetableFieldNumber());
    statement.setTtfnid(resolvedTtfnid);

    Long activeHearingYear = timetableHearingYearService.getActiveHearingYear().getTimetableYear();
    statement.setTimetableYear(activeHearingYear);

    return createStatement(statement, documents);
  }

  @Override
  public TimetableHearingStatementModel updateStatement(Long id, TimetableHearingStatementModel statement,
      List<MultipartFile> documents) {
    timetableHearingStatementService.getStatementById(id);

    TimetableHearingStatement statementUpdate = TimeTableHearingStatementMapper.toEntity(statement);
    addFilesToStatement(documents, statementUpdate);

    TimetableHearingStatement hearingStatement = timetableHearingStatementService.updateHearingStatement(statementUpdate);
    return TimeTableHearingStatementMapper.toModel(hearingStatement);
  }

  private void addFilesToStatement(List<MultipartFile> documents, TimetableHearingStatement statement) {
    if (documents != null) {
      log.info("Statement {}, adding {} documents", statement.getId() == null ? "new" : statement.getId(), documents.size());
      documents.forEach(multipartFile -> statement.getDocuments().add(StatementDocument.builder()
          .statement(statement)
          .fileName(multipartFile.getOriginalFilename())
          .fileSize(multipartFile.getSize())
          .build()));
    }
  }

}
