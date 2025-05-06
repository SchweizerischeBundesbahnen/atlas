package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementApiV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.service.UserService;
import ch.sbb.line.directory.entity.TimetableHearingYear;
import ch.sbb.line.directory.entity.TimetableHearingYear_;
import ch.sbb.line.directory.exception.ForbiddenDueToHearingYearSettingsException;
import ch.sbb.line.directory.exception.NoClientCredentialAuthUsedException;
import ch.sbb.line.directory.service.hearing.ResponsibleTransportCompaniesResolverService;
import ch.sbb.line.directory.service.hearing.TimetableFieldNumberResolverService;
import ch.sbb.line.directory.service.hearing.TimetableHearingYearService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TimetableHearingStatementControllerV2 implements TimetableHearingStatementApiV2 {

  private final TimetableHearingYearService timetableHearingYearService;
  private final TimetableFieldNumberResolverService timetableFieldNumberResolverService;
  private final ResponsibleTransportCompaniesResolverService responsibleTransportCompaniesResolverService;
  private final TimetableHearingStatementControllerInternal timetableHearingStatementControllerInternal;

  @Override
  public TimetableHearingStatementModelV2 createStatementExternal(TimetableHearingStatementModelV2 statement,
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

    return timetableHearingStatementControllerInternal.createStatement(statement, documents);
  }

}
