package ch.sbb.atlas.imports.servicepoint.trafficpoint;

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

}
