package ch.sbb.atlas.imports.servicepoint.trafficpoint;

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
public class TrafficPointCsvModelContainer {

  private String sloid;
  private List<TrafficPointElementCsvModel> trafficPointCsvModelList;

  public void mergeWhenDatesAreSequentialAndModelsAreEqual() {
    for (int csvModelIndex = 1; csvModelIndex < trafficPointCsvModelList.size(); csvModelIndex++) {
      final TrafficPointElementCsvModel current = trafficPointCsvModelList.get(csvModelIndex);
      final TrafficPointElementCsvModel previous = trafficPointCsvModelList.get(csvModelIndex - 1);

      if (DateHelper.areDatesSequential(previous.getValidTo(), current.getValidFrom()) && current.equals(previous)) {
        trafficPointCsvModelList.remove(csvModelIndex - 1);
        current.setValidFrom(previous.getValidFrom());
      }
    }
  }

}
