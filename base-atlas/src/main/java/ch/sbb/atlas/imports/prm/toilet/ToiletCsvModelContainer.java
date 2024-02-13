package ch.sbb.atlas.imports.prm.toilet;

import ch.sbb.atlas.api.prm.model.toilet.ToiletVersionModel;
import ch.sbb.atlas.imports.prm.BasePrmCsvModelContainer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ToiletCsvModelContainer extends BasePrmCsvModelContainer<ToiletCsvModel> {

  @JsonIgnore
  public List<ToiletVersionModel> getCreateModels() {
    return getCsvModels().stream().map(ToiletCsvToModelMapper::toModel).toList();
  }

}
