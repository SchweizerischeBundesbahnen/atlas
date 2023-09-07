package ch.sbb.atlas.imports.servicepoint.loadingpoint;

import ch.sbb.atlas.imports.servicepoint.BaseCsvModelContainer;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Getter
@SuperBuilder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoadingPointCsvModelContainer extends BaseCsvModelContainer<LoadingPointCsvModel> {

  private Integer didokCode;
  private Integer loadingPointNumber;

  @Override
  protected void logFoundVersionsToMerge() {
    log.info("Found versions to merge with didokCode|loadingPointNumber: {}", this.didokCode + "|" + this.loadingPointNumber);
  }
  public Integer getDidokCode(){
    return ServicePointNumber.removeCheckDigit(didokCode);
  }

}
