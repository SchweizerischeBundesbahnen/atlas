package ch.sbb.atlas.imports.prm.stoppoint;

import ch.sbb.atlas.api.prm.model.stoppoint.CreateStopPointVersionModel;
import ch.sbb.atlas.imports.prm.stoppoint.mapper.StopPointCsvToModelMapper;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
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
public class StopPointCsvModelContainer {

  private Integer didokCode;
  private List<StopPointCsvModel> stopPointCsvModels;
  private List<CreateStopPointVersionModel> createStopPointVersionModels;

  public Integer getDidokCode() {
    return ServicePointNumber.removeCheckDigit(this.didokCode);
  }

  public List<CreateStopPointVersionModel> getCreateStopPointVersionModels() {
    return stopPointCsvModels.stream().map(StopPointCsvToModelMapper::toModel).toList();
  }

}
