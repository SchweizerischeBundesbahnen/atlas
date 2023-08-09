package ch.sbb.atlas.imports.servicepoint.trafficpoint;

import ch.sbb.atlas.imports.servicepoint.BaseCsvModelContainer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Getter
@SuperBuilder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrafficPointCsvModelContainer extends BaseCsvModelContainer<TrafficPointElementCsvModel> {

  private String sloid;

  @Override
  protected void logFoundVersionsToMerge() {
    log.info("Found versions to merge with sloid: {}", this.sloid);
  }

}
