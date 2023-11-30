package ch.sbb.importservice.service.csv;

import static java.util.Comparator.comparing;

import ch.sbb.atlas.imports.prm.BasePrmCsvModel;
import ch.sbb.atlas.versioning.date.DateHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class PrmCsvService {

  private static final int ACTIVE_STATUS = 1;

  public static <T extends BasePrmCsvModel> List<T> filterForActive(List<T> models) {
    List<T> activeVersions = models.stream().filter(activeOnly()).toList();
    log.info("Found and removed {} inactive (STATUS=0) versions.", models.size() - activeVersions.size());
    return activeVersions;
  }

  private static Predicate<BasePrmCsvModel> activeOnly() {
    return basePrmCsvModel -> basePrmCsvModel.getStatus().equals(ACTIVE_STATUS);
  }

  public <T extends BasePrmCsvModel> PrmCsvMergeResult<T> mergeSequentialEqualVersions(List<T> csvModels) {
    List<T> stopPointCsvModelListMerged = new ArrayList<>();
    if (csvModels.size() == 1) {
      return new PrmCsvMergeResult<>(csvModels);
    }
    List<String> mergedSloids = new ArrayList<>();
    if (csvModels.size() > 1) {
      csvModels.sort(comparing(T::getValidFrom));
      stopPointCsvModelListMerged = new ArrayList<>(List.of(csvModels.get(0)));
      for (int currentIndex = 1; currentIndex < csvModels.size(); currentIndex++) {
        T previous = stopPointCsvModelListMerged.get(stopPointCsvModelListMerged.size() - 1);
        T current = csvModels.get(currentIndex);
        if (DateHelper.areDatesSequential(previous.getValidTo(), current.getValidFrom())
            && current.equals(previous)) {
          removeCurrentVersionIncreaseNextValidTo(previous, current);
          mergedSloids.add(current.getSloid());
        } else {
          stopPointCsvModelListMerged.add(current);
        }
      }
    }
    return new PrmCsvMergeResult<>(stopPointCsvModelListMerged, mergedSloids);
  }

  private void removeCurrentVersionIncreaseNextValidTo(BasePrmCsvModel previous, BasePrmCsvModel current) {
    log.info("Found versions to merge with number {}", previous.getSloid());
    log.info("Version-1 [{}]-[{}]", previous.getValidFrom(), previous.getValidTo());
    log.info("Version-2 [{}]-[{}]", current.getValidFrom(), current.getValidTo());
    previous.setValidTo(current.getValidTo());
    log.info("Version merged [{}]-[{}]", previous.getValidFrom(), current.getValidTo());
  }

  public <T extends BasePrmCsvModel> PrmCsvMergeResult<T> mergeEqualVersions(List<T> stopPointCsvModels) {
    List<T> csvModelListMerged = new ArrayList<>();
    if (stopPointCsvModels.size() == 1) {
      return new PrmCsvMergeResult<>(stopPointCsvModels);
    }
    List<String> mergedSloids = new ArrayList<>();
    if (stopPointCsvModels.size() > 1) {
      stopPointCsvModels.sort(comparing(T::getValidFrom));
      csvModelListMerged = new ArrayList<>(
          List.of(stopPointCsvModels.get(0))
      );
      for (int currentIndex = 1; currentIndex < stopPointCsvModels.size(); currentIndex++) {
        T previous = csvModelListMerged.get(csvModelListMerged.size() - 1);
        T current = stopPointCsvModels.get(currentIndex);
        if (current.getValidFrom().isEqual(previous.getValidFrom()) && current.getValidTo().isEqual(previous.getValidTo())
            && current.equals(previous)) {
          log.info("Found duplicated version with number {}", previous.getSloid());
          log.info("Version-1 [{}]-[{}]", previous.getValidFrom(), previous.getValidTo());
          log.info("Version-2 [{}]-[{}]", current.getValidFrom(), current.getValidTo());
          mergedSloids.add(current.getSloid());
        } else {
          csvModelListMerged.add(current);
        }
      }
    }
    return new PrmCsvMergeResult<>(csvModelListMerged, mergedSloids);
  }

  @Data
  @RequiredArgsConstructor
  @AllArgsConstructor
  public static class PrmCsvMergeResult<T> {

    private final List<T> versions;
    private List<String> mergedSloids = new ArrayList<>();
  }

}
