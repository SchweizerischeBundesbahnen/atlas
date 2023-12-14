package ch.sbb.atlas.imports.servicepoint.servicepoint;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.versioning.date.DateHelper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Comparator.comparing;

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
  private List<ServicePointCsvModel> servicePointCsvModelList;
  private boolean hasMergedVersionNotVirtualWithoutGeolocation;
  private boolean hasJustBezeichnungDiffMerged;

  public void mergeVersionsIsNotVirtualAndHasNotGeolocation() {
    if (this.servicePointCsvModelList.size() > 1) {
      this.servicePointCsvModelList.sort(comparing(ServicePointCsvModel::getValidFrom));
      final List<ServicePointCsvModel> servicePointCsvModelListMerged = new ArrayList<>(
          List.of(this.servicePointCsvModelList.get(0))
      );
      for (int currentIndex = 1; currentIndex < this.servicePointCsvModelList.size(); currentIndex++) {
        final ServicePointCsvModel previous = servicePointCsvModelListMerged.get(servicePointCsvModelListMerged.size() - 1);
        final ServicePointCsvModel current = this.servicePointCsvModelList.get(currentIndex);
        if (DateHelper.areDatesSequential(previous.getValidTo(), current.getValidFrom())
            && current.equals(previous)
            && checkIfMergeVersionsNotVirtualAndHasNotGeolocation(previous, current)) {
          this.hasMergedVersionNotVirtualWithoutGeolocation = true;
          removeCurrentVersionIncreaseNextValidTo(MERGE_NO_VIRTUAL_NO_GEOLOCATION, previous, current);
        } else {
          servicePointCsvModelListMerged.add(current);
        }
      }
      this.servicePointCsvModelList = servicePointCsvModelListMerged;
    }
  }

  public void mergeHasNotBezeichnungDiff() {
    if (this.servicePointCsvModelList.size() > 1) {
      this.servicePointCsvModelList.sort(comparing(ServicePointCsvModel::getValidFrom));
      final List<ServicePointCsvModel> servicePointCsvModelListMerged = new ArrayList<>(
          List.of(this.servicePointCsvModelList.get(0))
      );
      for (int currentIndex = 1; currentIndex < this.servicePointCsvModelList.size(); currentIndex++) {
        final ServicePointCsvModel previous = servicePointCsvModelListMerged.get(servicePointCsvModelListMerged.size() - 1);
        final ServicePointCsvModel current = this.servicePointCsvModelList.get(currentIndex);
        if (DateHelper.areDatesSequential(previous.getValidTo(), current.getValidFrom())
            && current.equals(previous)
            && !hasBezeichnungDiff(previous, current)) {
          this.hasJustBezeichnungDiffMerged = true;
          removeCurrentVersionIncreaseNextValidTo(MERGE_HAS_JUST_BEZEICHNUNG_DIFF, previous, current);
        } else {
          servicePointCsvModelListMerged.add(current);
        }
      }
      this.servicePointCsvModelList = servicePointCsvModelListMerged;
    }
  }

  boolean checkIfMergeVersionsNotVirtualAndHasNotGeolocation(ServicePointCsvModel previous, ServicePointCsvModel current) {
    return isNotVirtualAndHasNotGeolocation(previous) || isNotVirtualAndHasNotGeolocation(current);
  }

  private boolean isNotVirtualAndHasNotGeolocation(ServicePointCsvModel servicePointCsvModel) {
    return !servicePointCsvModel.getIsVirtuell() && hasNoGeolocation(servicePointCsvModel);
  }

  private boolean hasBezeichnungDiff(ServicePointCsvModel previous, ServicePointCsvModel current) {
    return Objects.nonNull(previous.getBezeichnung17())
        && Objects.nonNull(current.getBezeichnung17())
        && !previous.getBezeichnung17().equals(current.getBezeichnung17());
  }

  private boolean hasNoGeolocation(ServicePointCsvModel servicePointCsvModel) {
    return servicePointCsvModel.getELv03() == null
        && servicePointCsvModel.getNLv03() == null
        && servicePointCsvModel.getELv95() == null
        && servicePointCsvModel.getNLv95() == null
        && servicePointCsvModel.getEWgs84() == null
        && servicePointCsvModel.getNWgs84() == null;
  }

  private void removeCurrentVersionIncreaseNextValidTo(String mergeType, ServicePointCsvModel previous,
      ServicePointCsvModel current) {
    log.info(mergeType + "Found versions to merge with number {}", getDidokCode());
    log.info(mergeType + "Version-1 [{}]-[{}]", previous.getValidFrom(), previous.getValidTo());
    log.info(mergeType + "Version-2 [{}]-[{}]", current.getValidFrom(), current.getValidTo());
    previous.setValidTo(current.getValidTo());
    log.info(mergeType + "Version merged [{}]-[{}]", previous.getValidFrom(), current.getValidTo());
  }

  public Integer getDidokCode(){
    return ServicePointNumber.removeCheckDigit(this.didokCode);
  }

}
