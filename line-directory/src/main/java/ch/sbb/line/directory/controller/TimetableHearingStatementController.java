package ch.sbb.line.directory.controller;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementApiV1;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementRequestParams;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.api.timetable.hearing.model.UpdateHearingStatementStatusModel;
import ch.sbb.atlas.model.exception.BadRequestException;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.mapper.TimetableHearingStatementMapper;
import ch.sbb.line.directory.model.TimetableHearingStatementSearchRestrictions;
import ch.sbb.line.directory.service.hearing.ResponsibleTransportCompaniesResolverService;
import ch.sbb.line.directory.service.hearing.TimetableFieldNumberResolverService;
import ch.sbb.line.directory.service.hearing.TimetableHearingStatementExportService;
import ch.sbb.line.directory.service.hearing.TimetableHearingStatementService;
import ch.sbb.line.directory.service.hearing.TimetableHearingYearService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TimetableHearingStatementController implements TimetableHearingStatementApiV1 {

  private final TimetableHearingStatementService timetableHearingStatementService;
  private final TimetableHearingYearService timetableHearingYearService;
  private final TimetableFieldNumberResolverService timetableFieldNumberResolverService;
  private final ResponsibleTransportCompaniesResolverService responsibleTransportCompaniesResolverService;
  private final TimetableHearingStatementExportService timetableHearingStatementExportService;

  @Override
  public Container<TimetableHearingStatementModel> getStatements(Pageable pageable,
      TimetableHearingStatementRequestParams statementRequestParams) {
    Page<TimetableHearingStatement> hearingStatements = timetableHearingStatementService.getHearingStatements(
        TimetableHearingStatementSearchRestrictions.builder()
            .pageable(pageable)
            .statementRequestParams(statementRequestParams).build());
    List<TimetableHearingStatementModel> enrichedModels = timetableFieldNumberResolverService.resolveAdditionalVersionInfo(
        hearingStatements.stream().map(TimetableHearingStatementMapper::toModel).toList());
    return Container.<TimetableHearingStatementModel>builder()
        .objects(enrichedModels)
        .totalCount(hearingStatements.getTotalElements())
        .build();
  }

  @Override
  public Resource getStatementsAsCsv(String language, TimetableHearingStatementRequestParams statementRequestParams) {
    if (statementRequestParams.getTimetableHearingYear() == null) {
      throw new BadRequestException("timetableHearingYear is mandatory here");
    }
    if (!Set.of("de", "fr", "it").contains(language)) {
      throw new BadRequestException("Language must be either de,fr,it");
    }

    Container<TimetableHearingStatementModel> statements = getStatements(Pageable.unpaged(), statementRequestParams);
    File csvFile = timetableHearingStatementExportService.getStatementsAsCsv(statements.getObjects(), new Locale(language));

    try {
      return new InputStreamResource(new FileInputStream(csvFile));
    } catch (IOException e) {
      throw new FileException(e);
    }
  }

  @Override
  public TimetableHearingStatementModel getStatement(Long id) {
    return TimetableHearingStatementMapper.toModel(timetableHearingStatementService.getTimetableHearingStatementById(id));
  }

  @Override
  public Resource getStatementDocument(Long id, String filename) {
    File file = timetableHearingStatementService.getStatementDocument(id, filename);
    try {
      return new InputStreamResource(new FileInputStream(file));
    } catch (IOException e) {
      throw new FileException(e);
    }
  }

  @Override
  public void deleteStatementDocument(Long id, String filename) {
    timetableHearingStatementService.deleteStatementDocument(
        timetableHearingStatementService.getTimetableHearingStatementById(id), filename);
  }

  @Override
  public TimetableHearingStatementModel createStatement(TimetableHearingStatementModel statement, List<MultipartFile> documents) {
    return timetableHearingStatementService.createHearingStatement(statement, documents);
  }

  @Override
  public TimetableHearingStatementModel createStatementExternal(TimetableHearingStatementModel statement,
      List<MultipartFile> documents) {
    String resolvedTtfnid =
        timetableFieldNumberResolverService.resolveTtfnid(statement.getTimetableFieldNumber());
    statement.setTtfnid(resolvedTtfnid);

    List<TimetableHearingStatementResponsibleTransportCompanyModel> responsibleTransportCompanies =
        responsibleTransportCompaniesResolverService.resolveResponsibleTransportCompanies(
            resolvedTtfnid);
    statement.setResponsibleTransportCompanies(responsibleTransportCompanies);

    Long activeHearingYear = timetableHearingYearService.getActiveHearingYear().getTimetableYear();
    statement.setTimetableYear(activeHearingYear);

    return createStatement(statement, documents);
  }

  @Override
  public TimetableHearingStatementModel updateHearingStatement(Long id, TimetableHearingStatementModel statement,
      List<MultipartFile> documents) {
    statement.setId(id);

    TimetableHearingStatement hearingStatement = timetableHearingStatementService.updateHearingStatement(statement, documents);
    return TimetableHearingStatementMapper.toModel(hearingStatement);
  }

  @Override
  public void updateHearingStatementStatus(UpdateHearingStatementStatusModel updateHearingStatementStatus,
      StatementStatus statementStatus) {
    List<TimetableHearingStatement> timetableHearingStatements =
        timetableHearingStatementService.getTimetableHearingStatementsByIds(
            updateHearingStatementStatus.getIds());
    timetableHearingStatements.forEach(
        timetableHearingStatement -> timetableHearingStatementService.updateHearindStatementStatus(timetableHearingStatement,
            statementStatus, updateHearingStatementStatus.getJustification()));
  }

  @Override
  public List<TransportCompanyModel> getResponsibleTransportCompanies(String ttfnid, Long year) {
    LocalDate validOn = LocalDate.of(year.intValue(), 1, 1);
    return responsibleTransportCompaniesResolverService.getResponsibleTransportCompanies(ttfnid, validOn);
  }
}
