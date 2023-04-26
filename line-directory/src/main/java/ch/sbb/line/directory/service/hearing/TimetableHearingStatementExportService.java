package ch.sbb.line.directory.service.hearing;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.client.user.administration.UserAdministrationClient;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.api.user.administration.UserDisplayNameModel;
import ch.sbb.atlas.export.AtlasCsvMapper;
import ch.sbb.atlas.export.CsvExportWriter;
import ch.sbb.atlas.export.LocalizedPropertyNamingStrategy;
import ch.sbb.line.directory.model.csv.TimetableHearingStatementCsvModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TimetableHearingStatementExportService {

  private final FileService fileService;
  private final MessageSource timetableHearingStatementCsvTranslations;
  private final UserAdministrationClient userAdministrationClient;

  public File getStatementsAsCsv(List<TimetableHearingStatementModel> statements, Locale locale) {
    List<TimetableHearingStatementCsvModel> csvData = statements.stream().map(TimetableHearingStatementCsvModel::fromModel)
        .toList();

    Set<String> exportedEditors = csvData.stream().map(TimetableHearingStatementCsvModel::getEditor).collect(Collectors.toSet());
    List<UserDisplayNameModel> resolvedUserInformation = userAdministrationClient.getUserInformation(
        new ArrayList<>(exportedEditors));

    csvData.forEach(csvLine -> resolvedUserInformation.stream()
        .filter(i -> i.getSbbUserId().equals(csvLine.getEditor())).findFirst()
        .ifPresent(userInfo -> csvLine.setEditor(userInfo.getDisplayName())));

    return CsvExportWriter.writeToFile(fileService.getDir() + "statements", csvData,
        new AtlasCsvMapper(TimetableHearingStatementCsvModel.class,
            new LocalizedPropertyNamingStrategy(timetableHearingStatementCsvTranslations, locale)).getObjectWriter());
  }

}
