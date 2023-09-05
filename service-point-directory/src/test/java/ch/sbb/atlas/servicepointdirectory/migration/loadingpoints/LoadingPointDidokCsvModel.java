package ch.sbb.atlas.servicepointdirectory.migration.loadingpoints;

import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LoadingPointDidokCsvModel extends LoadingPointCsvModel {

  public String getServicePointNumberAndLoadingPointNumberKey() {
    return getServicePointNumberWithoutCheckDigit() + "-" + getNumber();
  }

  public Integer getServicePointNumberWithoutCheckDigit() {
    return getServicePointNumber() / 10;
  }

}
