package ch.sbb.importservice.service.csv;

import ch.sbb.atlas.imports.prm.platform.PlatformCsvModel;
import ch.sbb.atlas.imports.prm.platform.PlatformCsvModelContainer;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import ch.sbb.importservice.utils.JobDescriptionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ch.sbb.importservice.service.csv.CsvFileNameModel.SERVICEPOINT_DIDOK_DIR_NAME;

@Service
@Slf4j
public class PlatformCsvService extends PrmCsvService<PlatformCsvModel> {

  public static final String PRM_STOP_PLACES_FILE_NAME = "PRM_PLATFORMS";

  protected PlatformCsvService(FileHelperService fileHelperService, JobHelperService jobHelperService) {
    super(fileHelperService, jobHelperService);
  }

  @Override
  protected CsvFileNameModel csvFileNameModel() {
    return CsvFileNameModel.builder()
        .fileName(PRM_STOP_PLACES_FILE_NAME)
        .s3BucketDir(SERVICEPOINT_DIDOK_DIR_NAME)
        .addDateToPostfix(true)
        .build();
  }

  @Override
  protected String getModifiedDateHeader() {
    return EDITED_AT_COLUMN_NAME_PRM;
  }

  @Override
  protected String getImportCsvJobName() {
    return JobDescriptionConstants.IMPORT_PLATFORM_CSV_JOB_NAME;
  }

  @Override
  protected Class<PlatformCsvModel> getType() {
    return PlatformCsvModel.class;
  }

  public List<PlatformCsvModelContainer> mapToPlatformCsvModelContainers(List<PlatformCsvModel> platformCsvModels) {

    Map<String, List<PlatformCsvModel>> groupedPlatforms = filterForActive(platformCsvModels).stream()
        .collect(Collectors.groupingBy(PlatformCsvModel::getSloid));

    List<PlatformCsvModelContainer> result = new ArrayList<>(groupedPlatforms.entrySet().stream().map(toContainer()).toList());

    mergePlatforms(result);
    return result;
  }

  private static Function<Entry<String, List<PlatformCsvModel>>, PlatformCsvModelContainer> toContainer() {
    return entry -> PlatformCsvModelContainer.builder()
        .sloid(entry.getKey())
        .csvModels(entry.getValue())
        .build();
  }

  private void mergePlatforms(List<PlatformCsvModelContainer> platformCsvModelContainers) {
    mergeSequentialEqualsVersions(platformCsvModelContainers);
    mergeEqualsVersions(platformCsvModelContainers);
  }

  private void mergeSequentialEqualsVersions(List<PlatformCsvModelContainer> csvModelContainers) {
    log.info("Starting checking sequential equals Platform versions...");
    List<String> mergedSloids = new ArrayList<>();
    csvModelContainers.forEach(
        container -> {
          PrmCsvMergeResult<PlatformCsvModel> prmCsvMergeResult = mergeSequentialEqualVersions(
              container.getCsvModels());
          container.setCsvModels(prmCsvMergeResult.getVersions());
          mergedSloids.addAll(prmCsvMergeResult.getMergedSloids());
        });
    log.info("Total merged sequential Platform versions {}", mergedSloids.size());
    log.info("Merged Platform Sloids {}", mergedSloids);
  }

  private void mergeEqualsVersions(List<PlatformCsvModelContainer> csvModelContainers) {
    log.info("Starting checking equals Platform versions...");

    List<String> mergedSloids = new ArrayList<>();
    csvModelContainers.forEach(
        container -> {
          PrmCsvMergeResult<PlatformCsvModel> prmCsvMergeResult = mergeEqualVersions(container.getCsvModels());
          container.setCsvModels(prmCsvMergeResult.getVersions());
          mergedSloids.addAll(prmCsvMergeResult.getMergedSloids());
        });

    log.info("Total Merged equals Platform versions {}", mergedSloids.size());
    log.info("Merged equals Platform Sloids {}", mergedSloids);
  }

}
