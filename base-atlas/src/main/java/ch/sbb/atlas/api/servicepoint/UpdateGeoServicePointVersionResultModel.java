package ch.sbb.atlas.api.servicepoint;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@SuperBuilder
@FieldNameConstants
@Schema(name = "UpdateGeoServicePointVersionResult")
public class UpdateGeoServicePointVersionResultModel{

  private Long id;

  private String sloid;

  private ServicePointGeolocationReadModel currentServicePointGeolocation;

  private ServicePointGeolocationReadModel updatedServicePointGeolocation;

  private List<VersionDataRage> currentVersionsDataRange;

  private List<VersionDataRage> updatedVersionsDataRange;

  public boolean isHasNumberOfVersionsChanged(){
    if (currentVersionsDataRange != null && updatedVersionsDataRange != null){
      return currentVersionsDataRange.size() != updatedVersionsDataRange.size();
    }
    return false;
  }

  public boolean isHasMergedVersions(){
    if (currentVersionsDataRange != null && updatedVersionsDataRange != null){
      return updatedVersionsDataRange.size() < currentVersionsDataRange.size();
    }
    return false;
  }

  public boolean isHasAdditionalVersionsGenerated(){
    if (currentVersionsDataRange != null && updatedVersionsDataRange != null){
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
  }

  public String getResponseMessage() {
    String msg = "";
    if (!isHasNumberOfVersionsChanged()) {
      msg += "No versioning changes happened! ";
    } else {
      if (isHasAdditionalVersionsGenerated()) {
        msg += String.format("Generated additional versions: \nbefore %s \nafter %s", this.getCurrentVersionsDataRange(),
            this.getUpdatedVersionsDataRange());
      }
      if (this.isHasMergedVersions()) {
        msg += String.format("Generated additional versions: \nbefore %s \nafter %s", this.getCurrentVersionsDataRange(),
            this.getUpdatedVersionsDataRange());
      }
    }
    msg += getDiffServicePointGeolocationAsMessage(this.getCurrentServicePointGeolocation(),
        this.getUpdatedServicePointGeolocation());
    return msg;
  }

  public static String getDiffServicePointGeolocationAsMessage(ServicePointGeolocationReadModel current,
      ServicePointGeolocationReadModel updated) {
    DiffResult<ServicePointGeolocationReadModel> build = new DiffBuilder<>(current, updated, ToStringStyle.NO_CLASS_NAME_STYLE)
        .append("Height", current.getHeight(), updated.getHeight())
        .append("Country", current.getCountry(), updated.getCountry())
        .append("Canton", current.getSwissLocation().getCanton(), updated.getSwissLocation().getCanton())
        .append("SwissDistrictNumber", current.getSwissLocation().getDistrict().getFsoNumber(),
            updated.getSwissLocation().getDistrict().getFsoNumber())
        .append("SwissDistrictName",
            current.getSwissLocation().getDistrict().getDistrictName(),
            updated.getSwissLocation().getDistrict().getDistrictName())
        .append("SwissMunicipalityNumber",
            current.getSwissLocation().getLocalityMunicipality().getFsoNumber(),
            updated.getSwissLocation().getLocalityMunicipality().getFsoNumber())
        .append("SwissMunicipalityName",
            current.getSwissLocation().getLocalityMunicipality().getMunicipalityName(),
            updated.getSwissLocation().getLocalityMunicipality().getMunicipalityName())
        .append("SwissLocalityName",
            current.getSwissLocation().getLocalityMunicipality().getLocalityName(),
            updated.getSwissLocation().getLocalityMunicipality().getLocalityName())
        .build();
    return build.toString();
  }
}
