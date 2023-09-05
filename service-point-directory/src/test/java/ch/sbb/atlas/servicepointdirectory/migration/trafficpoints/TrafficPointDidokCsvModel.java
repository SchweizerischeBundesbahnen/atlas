package ch.sbb.atlas.servicepointdirectory.migration.trafficpoints;

import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointElementCsvModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrafficPointDidokCsvModel extends TrafficPointElementCsvModel {

  @JsonProperty("DS_BEZEICHNUNG_OFFIZIELL")
  private String designationOfficial;

  @JsonProperty("DS_GO_IDENTIFIKATION")
  private int servicePointBusinessOrganisation;

}
