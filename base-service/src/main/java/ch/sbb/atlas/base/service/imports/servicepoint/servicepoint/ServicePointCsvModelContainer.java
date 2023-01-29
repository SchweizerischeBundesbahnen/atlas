package ch.sbb.atlas.base.service.imports.servicepoint.servicepoint;

import static java.util.Comparator.comparing;

import ch.sbb.atlas.base.service.versioning.date.DateHelper;
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
  private Integer didokCode;

  private boolean hasMergedVersionNotVirtualWithoutGeolocation;

  private List<ServicePointCsvModel> servicePointCsvModelList;

  public void mergeVersionsWithIsNotVirtualAndHasNotGeolocation() {
    if (this.servicePointCsvModelList.size() > 1) {
      this.servicePointCsvModelList.sort(comparing(ServicePointCsvModel::getValidFrom));
      for (int i = 1; i < this.servicePointCsvModelList.size(); i++) {
        int currentIndex = i - 1;
        ServicePointCsvModel current = this.servicePointCsvModelList.get(currentIndex);
        ServicePointCsvModel next = this.servicePointCsvModelList.get(i);
        if (current.equals(next)
            && (isNotVirtualAndHasNotGeolocation(current) || isNotVirtualAndHasNotGeolocation(next))
            && DateHelper.areDatesSequential(current.getValidTo(), next.getValidFrom())) {
          this.hasMergedVersionNotVirtualWithoutGeolocation = true;
          log.info(MERGE_NO_VIRTUAL_NO_GEOLOCATION + "Found versions to merge with number {}", this.didokCode);
          log.info(MERGE_NO_VIRTUAL_NO_GEOLOCATION + "Version-1 [{}]-[{}]", current.getValidFrom(), current.getValidTo());
          log.info(MERGE_NO_VIRTUAL_NO_GEOLOCATION + "Version-2 [{}]-[{}]", next.getValidFrom(), next.getValidTo());
          next.setValidFrom(current.getValidFrom());
          log.info(MERGE_NO_VIRTUAL_NO_GEOLOCATION + "Version merged [{}]-[{}]", next.getValidFrom(), next.getValidTo());
          this.servicePointCsvModelList.remove(currentIndex);
        }
      }
    }
  }

  boolean isNotVirtualAndHasNotGeolocation(ServicePointCsvModel servicePointCsvModel) {
    return !servicePointCsvModel.getIsVirtuell()
        && hasGeolocation(servicePointCsvModel);
  }

  boolean hasGeolocation(ServicePointCsvModel servicePointCsvModel) {
    return servicePointCsvModel.getELv03() == null
        && servicePointCsvModel.getNLv03() == null
        && servicePointCsvModel.getELv95() == null
        && servicePointCsvModel.getNLv95() == null
        && servicePointCsvModel.getEWgs84() == null
        && servicePointCsvModel.getNWgs84() == null;
  }

}
