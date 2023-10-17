package ch.sbb.atlas.imports.prm.stopplace;

import ch.sbb.atlas.api.prm.model.stopplace.CreateStopPlaceVersionModel;
import ch.sbb.atlas.imports.prm.stopplace.mapper.StopPlaceCsvToModelMapper;
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
public class StopPlaceCsvModelContainer {

  private Integer didokCode;
  private List<StopPlaceCsvModel> stopPlaceCsvModels;
  private List<CreateStopPlaceVersionModel> createStopPlaceVersionModels;
  public Integer getDidokCode(){
    return ServicePointNumber.removeCheckDigit(this.didokCode);
  }

  public List<CreateStopPlaceVersionModel> getCreateStopPlaceVersionModels(){
    return stopPlaceCsvModels.stream().map(StopPlaceCsvToModelMapper::toModel).toList();
  }

}
