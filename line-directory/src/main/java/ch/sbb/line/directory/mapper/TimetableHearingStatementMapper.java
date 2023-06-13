package ch.sbb.line.directory.mapper;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.transport.company.entity.SharedTransportCompany;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimetableHearingStatementMapper {

  private final ResponsibleTransportCompanyMapper responsibleTransportCompanyMapper;

  public TimetableHearingStatement toEntity(TimetableHearingStatementModel statementModel) {
    TimetableHearingStatement timetableHearingStatement = TimetableHearingStatement.builder()
        .id(statementModel.getId())
        .statementStatus(statementModel.getStatementStatus())
        .timetableYear(statementModel.getTimetableYear())
        .ttfnid(statementModel.getTtfnid())
        .swissCanton(statementModel.getSwissCanton())
        .stopPlace(statementModel.getStopPlace())
        .statementSender(StatementSenderMapper.toEntity(statementModel.getStatementSender()))
        .statement(statementModel.getStatement())
        .documents(statementModel.getDocuments().stream().map(StatementDocumentMapper::toEntity).collect(Collectors.toSet()))
        .justification(statementModel.getJustification())
        .comment(statementModel.getComment())
        .version(statementModel.getEtagVersion())
        .build();
    timetableHearingStatement.setResponsibleTransportCompanies(
        statementModel.getResponsibleTransportCompanies().stream()
            .map(responsibleTransportCompanyMapper::toEntity)
            .collect(Collectors.toSet()));
    timetableHearingStatement.setResponsibleTransportCompaniesDisplay(transformToCommaSeparated(timetableHearingStatement));
    return timetableHearingStatement;
  }

  public static TimetableHearingStatementModel toModel(TimetableHearingStatement statement) {
    return TimetableHearingStatementModel.builder()
        .id(statement.getId())
        .statementStatus(statement.getStatementStatus())
        .timetableYear(statement.getTimetableYear())
        .ttfnid(statement.getTtfnid())
        .swissCanton(statement.getSwissCanton())
        .stopPlace(statement.getStopPlace())
        .responsibleTransportCompanies(getResponsibleTransportCompanies(statement))
        .responsibleTransportCompaniesDisplay(statement.getResponsibleTransportCompaniesDisplay())
        .statementSender(StatementSenderMapper.toModel(statement.getStatementSender()))
        .statement(statement.getStatement())
        .documents(statement.getDocuments().stream().map(StatementDocumentMapper::toModel).toList())
        .justification(statement.getJustification())
        .comment(statement.getComment())
        .creationDate(statement.getCreationDate())
        .creator(statement.getCreator())
        .editionDate(statement.getEditionDate())
        .editor(statement.getEditor())
        .etagVersion(statement.getVersion())
        .build();
  }

  public static String transformToCommaSeparated(TimetableHearingStatement statement) {
    List<String> sorted = statement.getResponsibleTransportCompanies()
        .stream()
        .map(SharedTransportCompany::getAbbreviation)
        .sorted()
        .toList();
    return String.join(", ", sorted);
  }

  private static List<TimetableHearingStatementResponsibleTransportCompanyModel> getResponsibleTransportCompanies(
      TimetableHearingStatement statement) {
    return statement.getResponsibleTransportCompanies().stream().map(ResponsibleTransportCompanyMapper::toModel).toList();
  }

}
