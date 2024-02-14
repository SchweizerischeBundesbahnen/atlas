package ch.sbb.importservice.service.csv;

import static ch.sbb.importservice.service.csv.CsvFileNameModel.SERVICEPOINT_DIDOK_DIR_NAME;

import ch.sbb.atlas.imports.prm.toilet.ToiletCsvModel;
import ch.sbb.atlas.imports.prm.toilet.ToiletCsvModelContainer;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import ch.sbb.importservice.utils.JobDescriptionConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ToiletCsvService extends PrmCsvService<ToiletCsvModel> {

  public static final String PRM_TOILET_FILE_NAME = "PRM_TOILETS";

  protected ToiletCsvService(FileHelperService fileHelperService, JobHelperService jobHelperService) {
    super(fileHelperService, jobHelperService);
  }

  @Override
  protected CsvFileNameModel csvFileNameModel() {
    return CsvFileNameModel.builder()
        .fileName(PRM_TOILET_FILE_NAME)
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
    return JobDescriptionConstants.IMPORT_TOILET_CSV_JOB_NAME;
  }

  @Override
  protected Class<ToiletCsvModel> getType() {
    return ToiletCsvModel.class;
  }

  public List<ToiletCsvModelContainer> mapToToiletCsvModelContainers(List<ToiletCsvModel> toiletCsvModels) {

    Map<String, List<ToiletCsvModel>> groupedToilets = filterForActive(toiletCsvModels).stream()
        .collect(Collectors.groupingBy(ToiletCsvModel::getSloid));

    List<ToiletCsvModelContainer> result = new ArrayList<>(groupedToilets.entrySet().stream().map(toContainer()).toList());

    mergeToilets(result);
    return result;
  }

  private static Function<Entry<String, List<ToiletCsvModel>>, ToiletCsvModelContainer> toContainer() {
    return entry -> ToiletCsvModelContainer.builder()
        .sloid(entry.getKey())
        .csvModels(entry.getValue())
        .build();
  }

  private void mergeToilets(List<ToiletCsvModelContainer> toiletCsvModelContainers) {
    mergeSequentialEqualsVersions(toiletCsvModelContainers);
    mergeEqualsVersions(toiletCsvModelContainers);
  }

  private void mergeSequentialEqualsVersions(List<ToiletCsvModelContainer> csvModelContainers) {
    log.info("Starting checking sequential equals Toilet versions...");
    List<String> mergedSloids = new ArrayList<>();
    csvModelContainers.forEach(
        container -> {
          PrmCsvMergeResult<ToiletCsvModel> prmCsvMergeResult = mergeSequentialEqualVersions(
              container.getCsvModels());
          container.setCsvModels(prmCsvMergeResult.getVersions());
          mergedSloids.addAll(prmCsvMergeResult.getMergedSloids());
        });
    log.info("Total merged sequential Toilet versions {}", mergedSloids.size());
    log.info("Merged Toilet Sloids {}", mergedSloids);
  }

  private void mergeEqualsVersions(List<ToiletCsvModelContainer> csvModelContainers) {
    log.info("Starting checking equals Toilet versions...");

    List<String> mergedSloids = new ArrayList<>();
    csvModelContainers.forEach(
        container -> {
          PrmCsvMergeResult<ToiletCsvModel> prmCsvMergeResult = mergeEqualVersions(container.getCsvModels());
          container.setCsvModels(prmCsvMergeResult.getVersions());
          mergedSloids.addAll(prmCsvMergeResult.getMergedSloids());
        });

    log.info("Total Merged equals Toilet versions {}", mergedSloids.size());
    log.info("Merged equals Toilet Sloids {}", mergedSloids);
  }

}
