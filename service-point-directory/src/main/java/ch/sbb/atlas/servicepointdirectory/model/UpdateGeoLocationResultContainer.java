package ch.sbb.atlas.servicepointdirectory.model;

import static ch.sbb.atlas.servicepointdirectory.service.georeference.ServicePointGeoLocationUtils.getDiffServicePointGeolocationAsMessage;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class UpdateGeoLocationResultContainer {

  private Long id;

  private String sloid;

  private ServicePointGeolocation currentServicePointGeolocation;

  private ServicePointGeolocation updatedServicePointGeolocation;

  private List<VersionDataRage> currentVersionsDataRange;

  private List<VersionDataRage> updatedVersionsDataRange;

  @AllArgsConstructor
  @Data
  @Builder
  public static class VersionDataRage {

    private LocalDate validFrom;
    private LocalDate validTo;

    public String toString() {
      return "DataRange(validFrom=" + this.getValidFrom() + " validTo=" + this.getValidTo() + ")";
    }
  }

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

  public static List<VersionDataRage> mapToUpdatedVersionDataRages(
      List<ReadServicePointVersionModel> readServicePointVersionModels) {
    List<VersionDataRage> updatedVersionsDataRange = new ArrayList<>(readServicePointVersionModels.stream()
        .map(servicePointVersionModel ->
            new VersionDataRage(servicePointVersionModel.getValidFrom(), servicePointVersionModel.getValidTo())
        ).toList());
    updatedVersionsDataRange.sort(Comparator.comparing(VersionDataRage::getValidFrom));
    return updatedVersionsDataRange;
  }

  public static List<VersionDataRage> mapToCurrentVersionDataRages(List<ServicePointVersion> currentVersions) {
    List<VersionDataRage> currentVersionsDataRange = new ArrayList<>(currentVersions.stream()
        .map(servicePointVersionModel ->
            new VersionDataRage(servicePointVersionModel.getValidFrom(), servicePointVersionModel.getValidTo())
        ).toList());
    currentVersionsDataRange.sort(Comparator.comparing(VersionDataRage::getValidFrom));
    return currentVersionsDataRange;
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
