package ch.sbb.line.directory.module.tth.mapper;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.line.directory.module.tth.entity.TimetableHearingStatement;
import java.util.List;
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
        .statementAnonymous(statementModel.isStatementAnonymous())
        .anonymousStatement(statementModel.getAnonymousStatement())
        .documents(statementModel.getDocuments().stream().map(StatementDocumentMapper::toEntity).collect(Collectors.toSet()))
        .publicComment(statementModel.getPublicComment())
        .internalComment(statementModel.getInternalComment())
        .topic(statementModel.getTopic())
        .cantonTransferComment(statementModel.getCantonTransferComment())
        .version(statementModel.getEtagVersion())
        .build();
    timetableHearingStatement.setResponsibleTransportCompanies(
        statementModel.getResponsibleTransportCompanies().stream()
            .map(responsibleTransportCompanyMapper::toEntity)
            .collect(Collectors.toSet()));
    timetableHearingStatement.setResponsibleTransportCompaniesDisplay(timetableHearingStatement.getTransportCompaniesCommaSeparated());
    return timetableHearingStatement;
  }

  public static TimetableHearingStatementModelV2 toModel(TimetableHearingStatement statement) {
    return TimetableHearingStatementModelV2.builder()
        .id(statement.getId())
        .statementStatus(statement.getStatementStatus())
        .timetableYear(statement.getTimetableYear())
        .ttfnid(statement.getTtfnid())
        .swissCanton(statement.getSwissCanton())
        .stopPlace(statement.getStopPlace())
        .responsibleTransportCompanies(getResponsibleTransportCompanies(statement))
        .responsibleTransportCompaniesDisplay(statement.getResponsibleTransportCompaniesDisplay())
        .statementSender(StatementSenderMapperV2.toModel(statement.getStatementSender()))
        .statement(statement.getStatement())
        .statementAnonymous(statement.isStatementAnonymous())
        .anonymousStatement(statement.getAnonymousStatement())
        .documents(statement.getDocuments().stream().map(StatementDocumentMapper::toModel).toList())
        .publicComment(statement.getPublicComment())
        .internalComment(statement.getInternalComment())
        .cantonTransferComment(statement.getCantonTransferComment())
        .dossierId(statement.getDossierId())
        .creationDate(statement.getCreationDate())
        .creator(statement.getCreator())
        .editionDate(statement.getEditionDate())
        .editor(statement.getEditor())
        .etagVersion(statement.getVersion())
        .build();
  }

  private static List<TimetableHearingStatementResponsibleTransportCompanyModel> getResponsibleTransportCompanies(
      TimetableHearingStatement statement) {
    return statement.getResponsibleTransportCompanies().stream().map(ResponsibleTransportCompanyMapper::toModel).toList();
  }

}
