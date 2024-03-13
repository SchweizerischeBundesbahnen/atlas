package ch.sbb.atlas.servicepointdirectory.migration.loadingpoints;

import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

//@Data
//@EqualsAndHashCode(callSuper = true)
//@ToString




@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LoadingPointDidokCsvModel extends LoadingPointCsvModel {

  public String getServicePointNumberAndLoadingPointNumberKey() {
    return getServicePointNumber() + "-" + getNumber();
  }

}
