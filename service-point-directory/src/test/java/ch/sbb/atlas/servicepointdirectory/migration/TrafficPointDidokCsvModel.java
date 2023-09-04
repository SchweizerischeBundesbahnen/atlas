package ch.sbb.atlas.servicepointdirectory.migration;

import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointElementCsvModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TrafficPointDidokCsvModel extends TrafficPointElementCsvModel {

  @JsonProperty("DS_BEZEICHNUNG_OFFIZIELL")
  private String designationOfficial;

  @JsonProperty("DS_GO_IDENTIFIKATION")
  private int servicePointBusinessOrganisation;

}
