package ch.sbb.timetable.hearing.mapper;

import ch.sbb.atlas.api.timetable.hearing.ExternalTimetableHearingStatementCreateModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.timetable.hearing.entity.TimetableHearingStatement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimetableHearingStatementMapperV2 {

  private final ResponsibleTransportCompanyMapper responsibleTransportCompanyMapper;

  public TimetableHearingStatement toEntity(TimetableHearingStatementModelV2 statementModel) {
    TimetableHearingStatement timetableHearingStatement = TimetableHearingStatement.builder()
        .id(statementModel.getId())
        .statementStatus(statementModel.getStatementStatus())
        .timetableYear(statementModel.getTimetableYear())
        .ttfnid(statementModel.getTtfnid())
        .swissCanton(statementModel.getSwissCanton())
        .stopPlace(statementModel.getStopPlace())
        .statementSender(StatementSenderMapperV2.toEntity(statementModel.getStatementSender()))
        .statement(statementModel.getStatement())
        .statementAnonymous(statementModel.getStatementAnonymous())
        .anonymousStatement(statementModel.getAnonymousStatement())
        .documents(statementModel.getDocuments().stream().map(StatementDocumentMapper::toEntity).collect(Collectors.toSet()))
        .publicComment(statementModel.getPublicComment())
        .internalComment(statementModel.getInternalComment())
        .topic(statementModel.getTopic())
        .cantonTransferComment(statementModel.getCantonTransferComment())
        .oldSwissCanton(statementModel.getOldSwissCanton())
        .version(statementModel.getEtagVersion())
        .build();
    timetableHearingStatement.setResponsibleTransportCompanyIds(
        statementModel.getResponsibleTransportCompanies().stream()
            .map(TimetableHearingStatementResponsibleTransportCompanyModel::getId)
            .collect(Collectors.toSet()));
    timetableHearingStatement.setResponsibleTransportCompaniesDisplay(
        getTransportCompaniesCommaSeparated(statementModel.getResponsibleTransportCompanies()));
    return timetableHearingStatement;
  }

  public static TimetableHearingStatementModelV2 fromExternalModel(ExternalTimetableHearingStatementCreateModel statement) {
    return TimetableHearingStatementModelV2.builder()
        .ttfnid(statement.getTtfnid())
        .timetableFieldNumber(statement.getTimetableFieldNumber())
        .swissCanton(statement.getSwissCanton())
        .stopPlace(statement.getStopPlace())
        .statement(statement.getStatement())
        .statementSender(statement.getStatementSender())
        .build();
  }

  public TimetableHearingStatementModelV2 toModel(TimetableHearingStatement statement) {
    return toModel(statement, getResponsibleTransportCompanies(statement));
  }

  public TimetableHearingStatementModelV2 toModel(TimetableHearingStatement statement,
      List<TimetableHearingStatementResponsibleTransportCompanyModel> responsibleTransportCompanies) {
    return TimetableHearingStatementModelV2.builder()
        .id(statement.getId())
        .statementStatus(statement.getStatementStatus())
        .timetableYear(statement.getTimetableYear())
        .ttfnid(statement.getTtfnid())
        .swissCanton(statement.getSwissCanton())
        .stopPlace(statement.getStopPlace())
        .responsibleTransportCompanies(responsibleTransportCompanies)
        .responsibleTransportCompaniesDisplay(statement.getResponsibleTransportCompaniesDisplay())
        .statementSender(StatementSenderMapperV2.toModel(statement.getStatementSender()))
        .statement(statement.getStatement())
        .statementAnonymous(statement.getStatementAnonymous())
        .anonymousStatement(statement.getAnonymousStatement())
        .dataProtectionChecked(statement.isDataProtectionChecked())
        .documents(statement.getDocuments().stream().map(StatementDocumentMapper::toModel).toList())
        .publicComment(statement.getPublicComment())
        .internalComment(statement.getInternalComment())
        .cantonTransferComment(statement.getCantonTransferComment())
        .oldSwissCanton(statement.getOldSwissCanton())
        .topic(statement.getTopic())
        .dossierId(statement.getDossierId())
        .creationDate(statement.getCreationDate())
        .creator(statement.getCreator())
        .editionDate(statement.getEditionDate())
        .editor(statement.getEditor())
        .etagVersion(statement.getVersion())
        .build();
  }

  private List<TimetableHearingStatementResponsibleTransportCompanyModel> getResponsibleTransportCompanies(
      TimetableHearingStatement statement) {
    return statement.getResponsibleTransportCompanyIds().stream().map(responsibleTransportCompanyMapper::toModel).toList();
  }

  public static String getTransportCompaniesCommaSeparated(
      Collection<TimetableHearingStatementResponsibleTransportCompanyModel> transportCompanies) {
    return transportCompanies.stream()
        .map(TimetableHearingStatementResponsibleTransportCompanyModel::getAbbreviation)
        .filter(Objects::nonNull)
        .sorted()
        .collect(Collectors.joining(", "));
  }

}
