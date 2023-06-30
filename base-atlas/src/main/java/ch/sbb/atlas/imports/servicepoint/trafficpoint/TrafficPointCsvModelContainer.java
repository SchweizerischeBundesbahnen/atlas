package ch.sbb.atlas.imports.servicepoint.trafficpoint;

import ch.sbb.atlas.versioning.date.DateHelper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.ArrayList;
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
public class TrafficPointCsvModelContainer {

  private String sloid;
  private List<TrafficPointElementCsvModel> trafficPointCsvModelList;

  public void mergeWhenDatesAreSequentialAndModelsAreEqual() {
    if (trafficPointCsvModelList.size() > 1) {
      final List<TrafficPointElementCsvModel> trafficPointCsvModelListMerged = new ArrayList<>(
          List.of(trafficPointCsvModelList.get(0))
      );
      for (int csvModelIndex = 1; csvModelIndex < trafficPointCsvModelList.size(); csvModelIndex++) {
        final TrafficPointElementCsvModel previous = trafficPointCsvModelListMerged.get(
            trafficPointCsvModelListMerged.size() - 1);
        final TrafficPointElementCsvModel current = trafficPointCsvModelList.get(csvModelIndex);

        if (DateHelper.areDatesSequential(previous.getValidTo(), current.getValidFrom()) && current.equals(previous)) {
          previous.setValidTo(current.getValidTo());
        } else {
          trafficPointCsvModelListMerged.add(current);
        }
      }
      trafficPointCsvModelList = trafficPointCsvModelListMerged;
    }
  }

}
