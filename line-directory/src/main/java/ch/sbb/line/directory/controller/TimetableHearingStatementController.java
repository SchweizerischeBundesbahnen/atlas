package ch.sbb.line.directory.controller;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementAlternatingModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementApiV1;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementRequestParams;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModelV2;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.atlas.api.timetable.hearing.model.UpdateHearingCantonModel;
import ch.sbb.atlas.api.timetable.hearing.model.UpdateHearingStatementStatusModel;
import ch.sbb.atlas.model.exception.BadRequestException;
import ch.sbb.atlas.service.UserService;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.entity.TimetableHearingYear;
import ch.sbb.line.directory.entity.TimetableHearingYear_;
import ch.sbb.line.directory.exception.ForbiddenDueToHearingYearSettingsException;
import ch.sbb.line.directory.exception.ForbiddenDueWrongStatementTimeTableYearException;
import ch.sbb.line.directory.exception.NoClientCredentialAuthUsedException;
import ch.sbb.line.directory.mapper.TimetableHearingStatementMapperV2;
import ch.sbb.line.directory.model.TimetableHearingStatementSearchRestrictions;
import ch.sbb.line.directory.service.hearing.ResponsibleTransportCompaniesResolverService;
import ch.sbb.line.directory.service.hearing.TimetableFieldNumberResolverService;
import ch.sbb.line.directory.service.hearing.TimetableHearingStatementAlternationService;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TimetableHearingStatementController implements TimetableHearingStatementApiV1 {

  private final TimetableHearingStatementService timetableHearingStatementService;
  private final TimetableHearingStatementAlternationService timetableHearingStatementAlternationService;
  private final TimetableHearingYearService timetableHearingYearService;
  private final TimetableFieldNumberResolverService timetableFieldNumberResolverService;
  private final ResponsibleTransportCompaniesResolverService responsibleTransportCompaniesResolverService;
  private final TimetableHearingStatementExportService timetableHearingStatementExportService;

  @Override
  public Container<TimetableHearingStatementModelV2> getStatements(Pageable pageable,
      TimetableHearingStatementRequestParams statementRequestParams) {
    Page<TimetableHearingStatement> hearingStatements = timetableHearingStatementService.getHearingStatements(
        TimetableHearingStatementSearchRestrictions.builder()
            .pageable(pageable)
            .statementRequestParams(statementRequestParams).build());
    List<TimetableHearingStatementModelV2> enrichedModels = timetableFieldNumberResolverService.resolveAdditionalVersionInfo(
        hearingStatements.stream().map(TimetableHearingStatementMapperV2::toModel).toList());
    return Container.<TimetableHearingStatementModelV2>builder()
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

    Container<TimetableHearingStatementModelV2> statements = getStatements(Pageable.unpaged(), statementRequestParams);
    File csvFile = timetableHearingStatementExportService.getStatementsAsCsv(statements.getObjects(), new Locale(language));

    try {
      return new InputStreamResource(new FileInputStream(csvFile));
    } catch (IOException e) {
      throw new FileException(e);
    }
  }

  @Override
  public TimetableHearingStatementModelV2 getStatement(Long id) {
    return TimetableHearingStatementMapperV2.toModel(timetableHearingStatementService.getTimetableHearingStatementById(id));
  }

  @Override
  public TimetableHearingStatementAlternatingModel getPreviousStatement(Long id, Pageable pageable,
      TimetableHearingStatementRequestParams statementRequestParams) {
    return timetableHearingStatementAlternationService.getPreviousStatement(id, pageable, statementRequestParams);
  }

  @Override
  public TimetableHearingStatementAlternatingModel getNextStatement(Long id, Pageable pageable,
      TimetableHearingStatementRequestParams statementRequestParams) {
    return timetableHearingStatementAlternationService.getNextStatement(id, pageable, statementRequestParams);
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
  public TimetableHearingStatementModelV2 createStatement(TimetableHearingStatementModelV2 statement, List<MultipartFile> documents) {
    TimetableHearingYear hearingYear = timetableHearingYearService.getHearingYear(statement.getTimetableYear());
    if (!hearingYear.isStatementCreatableInternal()) {
      throw new ForbiddenDueToHearingYearSettingsException(hearingYear.getTimetableYear(),
          TimetableHearingYear_.STATEMENT_CREATABLE_INTERNAL);
    }
    return timetableHearingStatementService.createHearingStatement(statement, documents);
  }

  @Override
  public TimetableHearingStatementModel createStatementExternal(TimetableHearingStatementModel statement,
      List<MultipartFile> documents) {
    Jwt accessToken = UserService.getAccessToken();
    if (!UserService.isClientCredentialAuthentication(accessToken)) {
      throw new NoClientCredentialAuthUsedException();
    }

    TimetableHearingYear activeHearingYear = timetableHearingYearService.getActiveHearingYear();
    statement.setTimetableYear(activeHearingYear.getTimetableYear());

    if (!activeHearingYear.isStatementCreatableExternal()) {
      throw new ForbiddenDueToHearingYearSettingsException(activeHearingYear.getTimetableYear(),
          TimetableHearingYear_.STATEMENT_CREATABLE_EXTERNAL);
    }

    String resolvedTtfnid =
        timetableFieldNumberResolverService.resolveTtfnid(statement.getTimetableFieldNumber());
    statement.setTtfnid(resolvedTtfnid);

    List<TimetableHearingStatementResponsibleTransportCompanyModel> responsibleTransportCompanies =
        responsibleTransportCompaniesResolverService.resolveResponsibleTransportCompanies(
            resolvedTtfnid);
    statement.setResponsibleTransportCompanies(responsibleTransportCompanies);


    TimetableHearingStatementModelV2 statementModelV2 = transformFromModelToModel2(statement);

    TimetableHearingStatementModelV2 modelV2 = createStatement(statementModelV2, documents);

    return transformFromModel2ToModel(modelV2);
  }

  @Override
  public TimetableHearingStatementModelV2 updateHearingStatement(Long id, TimetableHearingStatementModelV2 statement,
      List<MultipartFile> documents) {
    TimetableHearingYear hearingYear = timetableHearingYearService.getHearingYear(statement.getTimetableYear());
    if (!hearingYear.isStatementEditable()) {
      throw new ForbiddenDueToHearingYearSettingsException(
          hearingYear.getTimetableYear(),
          TimetableHearingYear_.STATEMENT_EDITABLE);
    }
    TimetableHearingStatement existingStatement = timetableHearingStatementService.getTimetableHearingStatementsById(id);
    statement.setId(id);
    TimetableHearingStatement hearingStatement = timetableHearingStatementService.updateHearingStatement(existingStatement,
        statement, documents);
    return TimetableHearingStatementMapperV2.toModel(hearingStatement);
  }

  @Override
  public void updateHearingStatementStatus(UpdateHearingStatementStatusModel updateHearingStatementStatus) {

    if (updateHearingStatementStatus.getIds().isEmpty()) {
      return;
    }

    List<TimetableHearingStatement> timetableHearingStatements =
        timetableHearingStatementService.getTimetableHearingStatementsByIds(
            updateHearingStatementStatus.getIds());

    long firstStatementTimetableYear = timetableHearingStatements.stream().findFirst().orElseThrow().getTimetableYear();

    TimetableHearingYear hearingYear = timetableHearingYearService.getHearingYear(firstStatementTimetableYear);

    boolean hasMoreThanOneTimetableYear =
        timetableHearingStatements.stream().map(TimetableHearingStatement::getTimetableYear).distinct().count() > 1;

    if (hasMoreThanOneTimetableYear) {
      throw new ForbiddenDueWrongStatementTimeTableYearException(
          firstStatementTimetableYear,
          TimetableHearingYear_.TIMETABLE_YEAR);
    }

    if (!hearingYear.isStatementEditable() || hearingYear.getHearingStatus() != HearingStatus.ACTIVE) {
      throw new ForbiddenDueToHearingYearSettingsException(
          hearingYear.getTimetableYear(),
          TimetableHearingYear_.STATEMENT_EDITABLE);
    }

    timetableHearingStatements.forEach(
        timetableHearingStatement -> timetableHearingStatementService.updateHearingStatementStatus(
            timetableHearingStatement,
            updateHearingStatementStatus.getStatementStatus(),
            updateHearingStatementStatus.getJustification()));
  }

  @Override
  public void updateHearingCanton(UpdateHearingCantonModel updateHearingCantonModel) {
    List<TimetableHearingStatement> timetableHearingStatements =
        timetableHearingStatementService.getTimetableHearingStatementsByIds(updateHearingCantonModel.getIds());
    timetableHearingStatements.forEach(
        timetableHearingStatement -> timetableHearingStatementService.updateHearingCanton(timetableHearingStatement,
            updateHearingCantonModel.getSwissCanton(), updateHearingCantonModel.getComment()));
  }

  @Override
  public List<TransportCompanyModel> getResponsibleTransportCompanies(String ttfnid, Long year) {
    LocalDate validOn = LocalDate.of(year.intValue(), 1, 1);
    return responsibleTransportCompaniesResolverService.getResponsibleTransportCompanies(ttfnid, validOn);
  }

  private TimetableHearingStatementModelV2 transformFromModelToModel2(TimetableHearingStatementModel statement) {
    TimetableHearingStatementSenderModelV2 statementSenderModelV2 = new TimetableHearingStatementSenderModelV2();
    statementSenderModelV2.setFirstName(statement.getStatementSender().getFirstName());
    statementSenderModelV2.setLastName(statement.getStatementSender().getLastName());
    statementSenderModelV2.setOrganisation(statement.getStatementSender().getOrganisation());
    statementSenderModelV2.setStreet(statement.getStatementSender().getStreet());
    statementSenderModelV2.setZip(statement.getStatementSender().getZip());
    statementSenderModelV2.setCity(statement.getStatementSender().getCity());
    statementSenderModelV2.setEmails(Set.of(statement.getStatementSender().getEmail()));

    TimetableHearingStatementModelV2 statementModelV2 = new TimetableHearingStatementModelV2();
    statementModelV2.setTimetableYear(statement.getTimetableYear());
    statementModelV2.setStatementStatus(statement.getStatementStatus());
    statementModelV2.setTtfnid(statement.getTtfnid());
    statementModelV2.setTimetableFieldNumber(statement.getTimetableFieldNumber());
    statementModelV2.setTimetableFieldDescription(statement.getTimetableFieldDescription());
    statementModelV2.setSwissCanton(statement.getSwissCanton());
    statementModelV2.setStopPlace(statement.getStopPlace());
    statementModelV2.setResponsibleTransportCompanies(statement.getResponsibleTransportCompanies());
    statementModelV2.setResponsibleTransportCompaniesDisplay(statement.getResponsibleTransportCompaniesDisplay());
    statementModelV2.setStatementSender(statementSenderModelV2);
    statementModelV2.setStatement(statement.getStatement());
    statementModelV2.setDocuments(statement.getDocuments());
    statementModelV2.setJustification(statement.getJustification());
    statementModelV2.setComment(statement.getComment());
    return statementModelV2;
  }

  private TimetableHearingStatementModel transformFromModel2ToModel(TimetableHearingStatementModelV2 statementV2) {
    TimetableHearingStatementSenderModel statementSenderModel = new TimetableHearingStatementSenderModel();
    statementSenderModel.setFirstName(statementV2.getStatementSender().getFirstName());
    statementSenderModel.setLastName(statementV2.getStatementSender().getLastName());
    statementSenderModel.setOrganisation(statementV2.getStatementSender().getOrganisation());
    statementSenderModel.setStreet(statementV2.getStatementSender().getStreet());
    statementSenderModel.setZip(statementV2.getStatementSender().getZip());
    statementSenderModel.setCity(statementV2.getStatementSender().getCity());
    statementSenderModel.setEmail(statementV2.getStatementSender().getEmails().iterator().next());

    TimetableHearingStatementModel statementModel = new TimetableHearingStatementModel();
    statementModel.setId(statementV2.getId());
    statementModel.setTimetableYear(statementV2.getTimetableYear());
    statementModel.setStatementStatus(statementV2.getStatementStatus());
    statementModel.setTtfnid(statementV2.getTtfnid());
    statementModel.setTimetableFieldNumber(statementV2.getTimetableFieldNumber());
    statementModel.setTimetableFieldDescription(statementV2.getTimetableFieldDescription());
    statementModel.setSwissCanton(statementV2.getSwissCanton());
    statementModel.setStopPlace(statementV2.getStopPlace());
    statementModel.setResponsibleTransportCompanies(statementV2.getResponsibleTransportCompanies());
    statementModel.setResponsibleTransportCompaniesDisplay(statementV2.getResponsibleTransportCompaniesDisplay());
    statementModel.setStatementSender(statementSenderModel);
    statementModel.setStatement(statementV2.getStatement());
    statementModel.setDocuments(statementV2.getDocuments());
    statementModel.setJustification(statementV2.getJustification());
    statementModel.setComment(statementV2.getComment());
    statementModel.setEtagVersion(statementV2.getEtagVersion());
    statementModel.setCreationDate(statementV2.getCreationDate());
    statementModel.setCreator(statementV2.getCreator());
    statementModel.setEditionDate(statementV2.getEditionDate());
    statementModel.setEditor(statementV2.getEditor());
    return statementModel;
  }

}
