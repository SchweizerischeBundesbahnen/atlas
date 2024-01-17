package ch.sbb.atlas.imports.prm.platform;

import ch.sbb.atlas.api.prm.model.platform.PlatformVersionModel;
import ch.sbb.atlas.imports.prm.BasePrmCsvModelContainer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlatformCsvModelContainer extends BasePrmCsvModelContainer<PlatformCsvModel> {

  @JsonIgnore
  public List<PlatformVersionModel> getCreateModels() {
    return getCsvModels().stream().map(PlatformCsvToModelMapper::toModel).toList();
  }

}
