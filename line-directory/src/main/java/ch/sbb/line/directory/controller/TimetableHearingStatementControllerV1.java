package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementApiV1;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV1;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModelV1;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModelV2;
import ch.sbb.atlas.service.UserService;
import ch.sbb.line.directory.entity.TimetableHearingYear;
import ch.sbb.line.directory.entity.TimetableHearingYear_;
import ch.sbb.line.directory.exception.ForbiddenDueToHearingYearSettingsException;
import ch.sbb.line.directory.exception.NoClientCredentialAuthUsedException;
import ch.sbb.line.directory.service.hearing.ResponsibleTransportCompaniesResolverService;
import ch.sbb.line.directory.service.hearing.TimetableFieldNumberResolverService;
import ch.sbb.line.directory.service.hearing.TimetableHearingStatementService;
import ch.sbb.line.directory.service.hearing.TimetableHearingYearService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TimetableHearingStatementControllerV1 implements TimetableHearingStatementApiV1 {

  private final TimetableHearingStatementService timetableHearingStatementService;
  private final TimetableHearingYearService timetableHearingYearService;
  private final TimetableFieldNumberResolverService timetableFieldNumberResolverService;
  private final ResponsibleTransportCompaniesResolverService responsibleTransportCompaniesResolverService;


//  @Override
  public TimetableHearingStatementModelV2 createStatement(TimetableHearingStatementModelV2 statement, List<MultipartFile> documents) {
    TimetableHearingYear hearingYear = timetableHearingYearService.getHearingYear(statement.getTimetableYear());
    if (!hearingYear.isStatementCreatableInternal()) {
      throw new ForbiddenDueToHearingYearSettingsException(hearingYear.getTimetableYear(),
          TimetableHearingYear_.STATEMENT_CREATABLE_INTERNAL);
    }
    return timetableHearingStatementService.createHearingStatement(statement, documents);
  }

  @Override
  public TimetableHearingStatementModelV1 createStatementExternal(TimetableHearingStatementModelV1 statement,
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

  private TimetableHearingStatementModelV1 transformFromModel2ToModel(TimetableHearingStatementModelV2 statementV2) {
    TimetableHearingStatementSenderModelV1 statementSenderModel = new TimetableHearingStatementSenderModelV1();
    statementSenderModel.setFirstName(statementV2.getStatementSender().getFirstName());
    statementSenderModel.setLastName(statementV2.getStatementSender().getLastName());
    statementSenderModel.setOrganisation(statementV2.getStatementSender().getOrganisation());
    statementSenderModel.setStreet(statementV2.getStatementSender().getStreet());
    statementSenderModel.setZip(statementV2.getStatementSender().getZip());
    statementSenderModel.setCity(statementV2.getStatementSender().getCity());
    statementSenderModel.setEmail(statementV2.getStatementSender().getEmails().iterator().next());

    TimetableHearingStatementModelV1 statementModel = new TimetableHearingStatementModelV1();
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

  private TimetableHearingStatementModelV2 transformFromModelToModel2(TimetableHearingStatementModelV1 statement) {
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

}
