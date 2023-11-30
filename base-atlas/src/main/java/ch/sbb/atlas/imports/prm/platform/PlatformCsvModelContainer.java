package ch.sbb.atlas.imports.prm.platform;

import ch.sbb.atlas.api.prm.model.platform.CreatePlatformVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
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
public class PlatformCsvModelContainer {

  private Integer didokCode;
  private List<PlatformCsvModel> platformCsvModels;
  private boolean hasMergedVersion;

  public Integer getDidokCode() {
    return ServicePointNumber.removeCheckDigit(this.didokCode);
  }

  public Collection<List<CreatePlatformVersionModel>> getModelsGroupedBySloid() {
    return platformCsvModels.stream().map(PlatformCsvToModelMapper::toModel)
        .collect(Collectors.groupingBy(CreatePlatformVersionModel::getSloid)).values();
  }

  public List<CreatePlatformVersionModel> getAllCreateModels() {
    return getModelsGroupedBySloid().stream().flatMap(Collection::stream).toList();
  }

}
