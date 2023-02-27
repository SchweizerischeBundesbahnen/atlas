package ch.sbb.atlas.timetable.hearing.controller;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementApiV1;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.timetable.hearing.entity.StatementDocument;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingStatement;
import ch.sbb.atlas.timetable.hearing.mapper.TimeTableHearingStatementMapper;
import ch.sbb.atlas.timetable.hearing.model.TimetableFieldNumberInformation;
import ch.sbb.atlas.timetable.hearing.service.TimetableFieldNumberResolverService;
import ch.sbb.atlas.timetable.hearing.service.TimetableHearingStatementService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TimetableHearingStatementController implements TimetableHearingStatementApiV1 {

  private final TimetableHearingStatementService timetableHearingStatementService;
  private final TimetableFieldNumberResolverService timetableFieldNumberResolverService;

  // TODO: getter für übersichten nach Jahr und Kanton

  @Override
  public TimetableHearingStatementModel createStatement(TimetableHearingStatementModel statement,
      List<MultipartFile> documents) {
    String resolvedTtfnid =
        timetableFieldNumberResolverService.resolveTtfnid(TimetableFieldNumberInformation.fromStatementModel(statement));
    statement.setTtfnid(resolvedTtfnid);

    TimetableHearingStatement statementToCreate = TimeTableHearingStatementMapper.toEntity(statement);
    addFilesToStatement(documents, statementToCreate);

    TimetableHearingStatement hearingStatement = timetableHearingStatementService.createHearingStatement(statementToCreate);
    return TimeTableHearingStatementMapper.toModel(hearingStatement);
  }

  @Override
  public TimetableHearingStatementModel updateStatement(TimetableHearingStatementModel statement,
      List<MultipartFile> documents) {
    String resolvedTtfnid =
        timetableFieldNumberResolverService.resolveTtfnid(TimetableFieldNumberInformation.fromStatementModel(statement));
    statement.setTtfnid(resolvedTtfnid);

    TimetableHearingStatement statementUpdate = TimeTableHearingStatementMapper.toEntity(statement);
    addFilesToStatement(documents, statementUpdate);

    TimetableHearingStatement hearingStatement = timetableHearingStatementService.updateHearingStatement(statementUpdate);
    return TimeTableHearingStatementMapper.toModel(hearingStatement);
  }

  private void addFilesToStatement(List<MultipartFile> documents, TimetableHearingStatement statement) {
    if (documents != null) {
      log.info("Statement {}, adding {} documents", statement.getId(), documents.size());
      documents.forEach(multipartFile -> statement.getDocuments().add(StatementDocument.builder()
          .fileName(multipartFile.getOriginalFilename())
          .fileSize(multipartFile.getSize())
          .build()));
    }
  }

}
