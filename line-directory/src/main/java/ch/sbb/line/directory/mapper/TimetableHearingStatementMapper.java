package ch.sbb.line.directory.mapper;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV1;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.line.directory.entity.SharedTransportCompany;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimetableHearingStatementMapper {

  private final ResponsibleTransportCompanyMapper responsibleTransportCompanyMapper;

  public TimetableHearingStatement toEntity(TimetableHearingStatementModelV1 statementModel) {
    TimetableHearingStatement timetableHearingStatement = TimetableHearingStatement.builder()
        .id(statementModel.getId())
        .statementStatus(statementModel.getStatementStatus())
        .timetableYear(statementModel.getTimetableYear())
        .ttfnid(statementModel.getTtfnid())
        .swissCanton(statementModel.getSwissCanton())
        .oldSwissCanton(statementModel.getOldSwissCanton())
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

  public static TimetableHearingStatementModelV1 toModel(TimetableHearingStatement statement) {
    return TimetableHearingStatementModelV1.builder()
        .id(statement.getId())
        .statementStatus(statement.getStatementStatus())
        .timetableYear(statement.getTimetableYear())
        .ttfnid(statement.getTtfnid())
        .swissCanton(statement.getSwissCanton())
        .oldSwissCanton(statement.getOldSwissCanton())
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
        .filter(Objects::nonNull)
        .sorted()
        .toList();
    return String.join(", ", sorted);
  }

  private static List<TimetableHearingStatementResponsibleTransportCompanyModel> getResponsibleTransportCompanies(
      TimetableHearingStatement statement) {
    return statement.getResponsibleTransportCompanies().stream().map(ResponsibleTransportCompanyMapper::toModel).toList();
  }

}
