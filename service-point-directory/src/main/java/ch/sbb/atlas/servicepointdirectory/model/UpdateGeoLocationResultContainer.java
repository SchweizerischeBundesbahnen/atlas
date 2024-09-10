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

  private List<VersionDataRange> currentVersionsDataRange;

  private List<VersionDataRange> updatedVersionsDataRange;

  @AllArgsConstructor
  @Data
  @Builder
  public static class VersionDataRange {

    private LocalDate validFrom;
    private LocalDate validTo;

    public String toString() {
      return "DataRange(validFrom=" + this.getValidFrom() + " validTo=" + this.getValidTo() + ")";
    }
  }

  public boolean hasNumberOfVersionsChanged() {
    if (currentVersionsDataRange != null && updatedVersionsDataRange != null) {
      return currentVersionsDataRange.size() != updatedVersionsDataRange.size();
    }
    return false;
  }

  public boolean hasMergedVersions() {
    if (currentVersionsDataRange != null && updatedVersionsDataRange != null) {
      return updatedVersionsDataRange.size() < currentVersionsDataRange.size();
    }
    return false;
  }

  public boolean hasAdditionalVersionsGenerated() {
    if (currentVersionsDataRange != null && updatedVersionsDataRange != null) {
      return updatedVersionsDataRange.size() > currentVersionsDataRange.size();
    }
    return false;
  }

  public static List<VersionDataRange> mapToUpdatedVersionDataRages(
      List<ReadServicePointVersionModel> readServicePointVersionModels) {
    List<VersionDataRange> updatedVersionsDataRange = new ArrayList<>(readServicePointVersionModels.stream()
        .map(servicePointVersionModel ->
            new VersionDataRange(servicePointVersionModel.getValidFrom(), servicePointVersionModel.getValidTo())
        ).toList());
    updatedVersionsDataRange.sort(Comparator.comparing(VersionDataRange::getValidFrom));
    return updatedVersionsDataRange;
  }

  public static List<VersionDataRange> mapToCurrentVersionDataRages(List<ServicePointVersion> currentVersions) {
    List<VersionDataRange> currentVersionsDataRange = new ArrayList<>(currentVersions.stream()
        .map(servicePointVersionModel ->
            new VersionDataRange(servicePointVersionModel.getValidFrom(), servicePointVersionModel.getValidTo())
        ).toList());
    currentVersionsDataRange.sort(Comparator.comparing(VersionDataRange::getValidFrom));
    return currentVersionsDataRange;
  }

  public String getResponseMessage() {
    StringBuilder msgBuilder = new StringBuilder();
    if (!hasNumberOfVersionsChanged()) {
      msgBuilder.append("No versioning changes happened!<br> ");
    } else {
      if (hasAdditionalVersionsGenerated()) {
        msgBuilder.append(
            String.format("Generated additional versions: <br>before %s <br>after %s", this.getCurrentVersionsDataRange(),
                this.getUpdatedVersionsDataRange()));
      }
      if (this.hasMergedVersions()) {
        msgBuilder.append(String.format("Merged versions: <br>before %s <br>after %s", this.getCurrentVersionsDataRange(),
            this.getUpdatedVersionsDataRange()));
      }
    }
    msgBuilder.append(getDiffServicePointGeolocationAsMessage(this.getCurrentServicePointGeolocation(),
        this.getUpdatedServicePointGeolocation()));
    return msgBuilder.toString();
  }

}
