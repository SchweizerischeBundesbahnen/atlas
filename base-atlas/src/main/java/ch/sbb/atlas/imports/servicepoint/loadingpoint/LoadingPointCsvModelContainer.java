package ch.sbb.atlas.imports.servicepoint.loadingpoint;

import ch.sbb.atlas.imports.servicepoint.BaseCsvModelContainer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@SuperBuilder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoadingPointCsvModelContainer extends BaseCsvModelContainer<LoadingPointCsvModel> {

  private Integer didokCode;
  private Integer loadingPointNumber;

}
