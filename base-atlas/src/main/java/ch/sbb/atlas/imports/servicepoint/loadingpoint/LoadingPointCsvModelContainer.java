package ch.sbb.atlas.imports.servicepoint.loadingpoint;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LoadingPointCsvModelContainer {

  private Integer didokCode;
  private Integer loadingPointNumber;
  private List<LoadingPointCsvModel> loadingPointCsvModelList;

}
