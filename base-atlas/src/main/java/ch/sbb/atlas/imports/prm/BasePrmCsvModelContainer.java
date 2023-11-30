package ch.sbb.atlas.imports.prm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BasePrmCsvModelContainer<T extends BasePrmCsvModel> {

  private String sloid;
  private List<T> csvModels;

}
