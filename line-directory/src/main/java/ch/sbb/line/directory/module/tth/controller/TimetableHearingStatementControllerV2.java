package ch.sbb.line.directory.module.tth.controller;

import ch.sbb.atlas.api.timetable.hearing.ExternalTimetableHearingStatementCreateModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementApiV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.service.UserService;
import ch.sbb.line.directory.module.tth.entity.TimetableHearingYear;
import ch.sbb.line.directory.module.tth.entity.TimetableHearingYear_;
import ch.sbb.line.directory.module.tth.exception.ForbiddenDueToHearingYearSettingsException;
import ch.sbb.line.directory.module.tth.exception.NoClientCredentialAuthUsedException;
import ch.sbb.line.directory.module.tth.mapper.TimetableHearingStatementMapperV2;
import ch.sbb.line.directory.module.tth.service.ResponsibleTransportCompaniesResolverService;
import ch.sbb.line.directory.module.tth.service.TimetableFieldNumberResolverService;
import ch.sbb.line.directory.module.tth.service.TimetableHearingYearService;
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
  public TimetableHearingStatementModelV2 createStatementExternal(ExternalTimetableHearingStatementCreateModel statement,
      List<MultipartFile> documents) {
    Jwt accessToken = UserService.getAccessToken();
    if (!UserService.isClientCredentialAuthentication(accessToken)) {
      throw new NoClientCredentialAuthUsedException();
    }
    TimetableHearingStatementModelV2 timetableHearingStatementModelV2 = TimetableHearingStatementMapperV2.fromExternalModel(
        statement);

    TimetableHearingYear activeHearingYear = timetableHearingYearService.getActiveHearingYear();
    timetableHearingStatementModelV2.setTimetableYear(activeHearingYear.getTimetableYear());

    if (!activeHearingYear.isStatementCreatableExternal()) {
      throw new ForbiddenDueToHearingYearSettingsException(activeHearingYear.getTimetableYear(),
          TimetableHearingYear_.STATEMENT_CREATABLE_EXTERNAL);
    }

    String resolvedTtfnid =
        timetableFieldNumberResolverService.resolveTtfnid(timetableHearingStatementModelV2.getTimetableFieldNumber());
    timetableHearingStatementModelV2.setTtfnid(resolvedTtfnid);

    List<TimetableHearingStatementResponsibleTransportCompanyModel> responsibleTransportCompanies =
        responsibleTransportCompaniesResolverService.resolveResponsibleTransportCompanies(
            resolvedTtfnid);
    timetableHearingStatementModelV2.setResponsibleTransportCompanies(responsibleTransportCompanies);

    return timetableHearingStatementControllerInternal.createStatement(timetableHearingStatementModelV2, documents);
  }

}
