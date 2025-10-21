package ch.sbb.line.directory.module.ttfn.service.quovadis;

import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.module.ttfn.repository.TimetableFieldNumberVersionRepository;
import ch.sbb.line.directory.module.ttfn.service.TimetableFieldNumberService;
import ch.sbb.line.directory.module.ttfn.service.quovadis.QuoVadisCsvReader.QuoVadisDataRow;
import ch.sbb.line.directory.module.ttfn.service.quovadis.QuoVadisDataMapper.TimetableFieldNumberV2;
import java.io.File;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuoVadisDataImportService {

  static final LocalDate FIRST_DAY_OF_FP_2026 = LocalDate.of(2025, 12, 14);

  private final TimetableFieldNumberVersionRepository timetableFieldNumberVersionRepository;
  private final TimetableFieldNumberService timetableFieldNumberService;

  @Transactional
  public void importDataFromQuoVadis(File file) {
    List<QuoVadisDataRow> quoVadisDataRows = QuoVadisCsvReader.parseFile(file);
    log.info("Parsed {} rows", quoVadisDataRows.size());

    List<TimetableFieldNumberV2> timetableFieldNumbers = QuoVadisDataMapper.mapToTimetableFieldNumber(quoVadisDataRows);
    log.info("Mappped to {} timetableFieldNumbers", timetableFieldNumbers.size());

    performDataMigration(timetableFieldNumbers);
  }

  private void performDataMigration(List<TimetableFieldNumberV2> newTtfns) {
    // Delete all versions which would start on or after Timetable-Year-Change
    timetableFieldNumberVersionRepository.deleteVersionsValidFromAfter(FIRST_DAY_OF_FP_2026);
    // Shorten all last versions to FP 2025 end date
    timetableFieldNumberVersionRepository.updateLastVersionsByTerminating();

    int numberOfUpdates = 0;
    int numberOfCreates = 0;
    List<TimetableFieldNumberVersion> versionsInDb = timetableFieldNumberVersionRepository.findAll();
    for (TimetableFieldNumberV2 timetableFieldNumber : newTtfns) {
      List<TimetableFieldNumberVersion> currentTtfn = versionsInDb.stream()
          .filter(i -> i.getNumber().equals(timetableFieldNumber.getNumber()))
          .sorted(Comparator.comparing(TimetableFieldNumberVersion::getValidFrom))
          .toList();
      if (!currentTtfn.isEmpty()) {
        numberOfUpdates++;
        //        TimetableFieldNumberVersion currentVersion = currentTtfn.getLast();
        //        TimetableFieldNumberVersion editedVersion = QuoVadisToAtlasMapper.toEntity(timetableFieldNumber);
        //        editedVersion.setVersion(currentVersion.getVersion());
        //        timetableFieldNumberService.update(currentVersion, editedVersion, currentTtfn);
      } else {
        numberOfCreates++;
        //        timetableFieldNumberService.create(QuoVadisToAtlasMapper.toEntity(timetableFieldNumber));
      }
    }

    log.info("Number of updates: {}", numberOfUpdates);
    log.info("Number of creates: {}", numberOfCreates);
  }

}
