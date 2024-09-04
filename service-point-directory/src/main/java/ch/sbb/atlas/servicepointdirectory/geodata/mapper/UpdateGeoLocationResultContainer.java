package ch.sbb.atlas.servicepointdirectory.geodata.mapper;

import static ch.sbb.atlas.servicepointdirectory.service.georeference.ServicePointGeoLocationUtils.getDiffServicePointGeolocationAsMessage;

import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@SuperBuilder
@FieldNameConstants
public class UpdateGeoLocationResultContainer {

  private Long id;

  private String sloid;

  private ServicePointGeolocation currentServicePointGeolocation;

  private ServicePointGeolocation updatedServicePointGeolocation;

  private List<VersionDataRage> currentVersionsDataRange;

  private List<VersionDataRage> updatedVersionsDataRange;

  public boolean isHasNumberOfVersionsChanged() {
    if (currentVersionsDataRange != null && updatedVersionsDataRange != null) {
      return currentVersionsDataRange.size() != updatedVersionsDataRange.size();
    }
    return false;
  }

  public boolean isHasMergedVersions() {
    if (currentVersionsDataRange != null && updatedVersionsDataRange != null) {
      return updatedVersionsDataRange.size() < currentVersionsDataRange.size();
    }
    return false;
  }

  public boolean isHasAdditionalVersionsGenerated() {
    if (currentVersionsDataRange != null && updatedVersionsDataRange != null) {
      return updatedVersionsDataRange.size() > currentVersionsDataRange.size();
    }
    return false;
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @EqualsAndHashCode
  @SuperBuilder
  public static class VersionDataRage {

    private LocalDate validFrom;
    private LocalDate validTo;

    public String toString() {
      return "DataRange(validFrom=" + this.getValidFrom() + " validTo=" + this.getValidTo() + ")";
    }
  }

  public String getResponseMessage() {
    String msg = "";
    if (!isHasNumberOfVersionsChanged()) {
      msg += "No versioning changes happened!<br> ";
    } else {
      if (isHasAdditionalVersionsGenerated()) {
        msg += String.format("Generated additional versions: <br>before %s <br>after %s", this.getCurrentVersionsDataRange(),
            this.getUpdatedVersionsDataRange());
      }
      if (this.isHasMergedVersions()) {
        msg += String.format("Merged versions: <br>before %s <br>after %s", this.getCurrentVersionsDataRange(),
            this.getUpdatedVersionsDataRange());
      }
    }
    msg += getDiffServicePointGeolocationAsMessage(this.getCurrentServicePointGeolocation(),
        this.getUpdatedServicePointGeolocation());
    return msg;
  }

}
