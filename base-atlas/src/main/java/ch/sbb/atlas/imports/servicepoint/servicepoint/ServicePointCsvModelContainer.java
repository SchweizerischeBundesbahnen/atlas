package ch.sbb.atlas.imports.servicepoint.servicepoint;

import static java.util.Comparator.comparing;

import ch.sbb.atlas.versioning.date.DateHelper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServicePointCsvModelContainer {

  private static final String MERGE_NO_VIRTUAL_NO_GEOLOCATION = "[MERGE-NO-VIRTUAL-NO-GEOLOCATION] - ";
  private static final String MERGE_HAS_JUST_BEZEICHNUNG_DIFF = "[MERGE-HAS-JUST-BEZEICHNUNG-DIFF] - ";
  private Integer didokCode;

  private boolean hasMergedVersionNotVirtualWithoutGeolocation;

  private List<ServicePointCsvModel> servicePointCsvModelList;
  private boolean hasJustBezeichnungDiffMerged;

  public void mergeVersionsIsNotVirtualAndHasNotGeolocation() {
    if (this.servicePointCsvModelList.size() > 1) {
      this.servicePointCsvModelList.sort(comparing(ServicePointCsvModel::getValidFrom));
      for (int i = 1; i < this.servicePointCsvModelList.size(); i++) {
        int currentIndex = i - 1;
        ServicePointCsvModel current = this.servicePointCsvModelList.get(currentIndex);
        ServicePointCsvModel next = this.servicePointCsvModelList.get(i);
        if (DateHelper.areDatesSequential(current.getValidTo(), next.getValidFrom()) && current.equals(next)) {
          checkIfMergeVersionsNotVirtualAndHasNotGeolocation(current, next, currentIndex);
        }
      }
    }
  }

  public void mergeHasJustBezeichnungDiff() {
    if (this.servicePointCsvModelList.size() > 1) {
      this.servicePointCsvModelList.sort(comparing(ServicePointCsvModel::getValidFrom));
      for (int i = 1; i < this.servicePointCsvModelList.size(); i++) {
        int currentIndex = i - 1;
        ServicePointCsvModel current = this.servicePointCsvModelList.get(currentIndex);
        ServicePointCsvModel next = this.servicePointCsvModelList.get(i);
        if (DateHelper.areDatesSequential(current.getValidTo(), next.getValidFrom()) && current.equals(next)) {
          checkIfMergeHasJustBezeichnungDiff(current, next, currentIndex);
        }
      }
    }
  }

  void checkIfMergeVersionsNotVirtualAndHasNotGeolocation(ServicePointCsvModel current, ServicePointCsvModel next,
      int currentIndex) {
    if ((isNotVirtualAndHasNotGeolocation(current) || isNotVirtualAndHasNotGeolocation(next))) {
      this.hasMergedVersionNotVirtualWithoutGeolocation = true;
      removeCurrentVersionIncreaseNextValidTo(MERGE_NO_VIRTUAL_NO_GEOLOCATION, current, next, currentIndex);
    }
  }

  private void checkIfMergeHasJustBezeichnungDiff(ServicePointCsvModel current, ServicePointCsvModel next,
      int currentIndex) {
    if (hasJustBezeichnungDiff(current, next)) {
      this.hasJustBezeichnungDiffMerged = true;
      removeCurrentVersionIncreaseNextValidTo(MERGE_HAS_JUST_BEZEICHNUNG_DIFF, current, next, currentIndex);

    }
  }

  private boolean isNotVirtualAndHasNotGeolocation(ServicePointCsvModel servicePointCsvModel) {
    return !servicePointCsvModel.getIsVirtuell()
        && hasGeolocation(servicePointCsvModel);
  }

  private boolean hasJustBezeichnungDiff(ServicePointCsvModel corrent, ServicePointCsvModel next) {
    return (corrent.getBezeichnung17() == null && next.getBezeichnung17() != null)
        || (corrent.getBezeichnung17() != null && next.getBezeichnung17() == null);
  }

  private boolean hasGeolocation(ServicePointCsvModel servicePointCsvModel) {
    return servicePointCsvModel.getELv03() == null
        && servicePointCsvModel.getNLv03() == null
        && servicePointCsvModel.getELv95() == null
        && servicePointCsvModel.getNLv95() == null
        && servicePointCsvModel.getEWgs84() == null
        && servicePointCsvModel.getNWgs84() == null;
  }

  private void removeCurrentVersionIncreaseNextValidTo(String mergeType, ServicePointCsvModel current,
      ServicePointCsvModel next, int currentIndex) {
    log.info(mergeType + "Found versions to merge with number {}", this.didokCode);
    log.info(mergeType + "Version-1 [{}]-[{}]", current.getValidFrom(), current.getValidTo());
    log.info(mergeType + "Version-2 [{}]-[{}]", next.getValidFrom(), next.getValidTo());
    next.setValidFrom(current.getValidFrom());
    log.info(mergeType + "Version merged [{}]-[{}]", next.getValidFrom(), next.getValidTo());
    this.servicePointCsvModelList.remove(currentIndex);
  }

}
