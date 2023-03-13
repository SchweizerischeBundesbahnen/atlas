package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.model.SwissCanton;
import ch.sbb.atlas.servicepointdirectory.model.CoordinatePair;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.util.Map;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "ServicePointGeolocation")
public class ServicePointGeolocationModel extends GeolocationModel {

  @JsonIgnore
  private Country country;
  private SwissLocation swissLocation;

  public static ServicePointGeolocationModel fromEntity(ServicePointGeolocation servicePointGeolocation) {
    if (servicePointGeolocation == null) {
      return null;
    }
    Map<SpatialReference, CoordinatePair> coordinates = getTransformedCoordinates(servicePointGeolocation);
    return ServicePointGeolocationModel.builder()
        .country(servicePointGeolocation.getCountry())
        .swissLocation(SwissLocation.builder()
            .canton(servicePointGeolocation.getSwissCanton())
            .cantonInformation(getCanton(servicePointGeolocation))
            .district(DistrictModel.builder()
                .fsoNumber(servicePointGeolocation.getSwissDistrictNumber())
                .districtName(servicePointGeolocation.getSwissDistrictName())
                .build())
            .localityMunicipality(LocalityMunicipalityModel.builder()
                .fsoNumber(servicePointGeolocation.getSwissMunicipalityNumber())
                .municipalityName(servicePointGeolocation.getSwissMunicipalityName())
                .localityName(servicePointGeolocation.getSwissLocalityName())
                .build())
            .build())
        .spatialReference(servicePointGeolocation.getSpatialReference())
        .lv95(coordinates.get(SpatialReference.LV95))
        .wgs84(coordinates.get(SpatialReference.WGS84))
        .wgs84web(coordinates.get(SpatialReference.WGS84WEB))
        .height(servicePointGeolocation.getHeight())
        .build();
  }

  private static Canton getCanton(ServicePointGeolocation servicePointGeolocation) {
    if (servicePointGeolocation.getSwissCanton() == null) {
      return null;
    }
    return Canton.builder()
        .abbreviation(servicePointGeolocation.getSwissCanton().getAbbreviation())
        .fsoNumber(servicePointGeolocation.getSwissCanton().getNumber())
        .name(servicePointGeolocation.getSwissCanton().getName())
        .build();
  }

  @JsonInclude
  @Schema(description = "ISO 2 abbreviation of the country, based on coordinates", example = "CH", accessMode =
      AccessMode.READ_ONLY)
  public String getIsoCountryCode() {
    return getCountry().getIsoCode();
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @SuperBuilder
  @Schema(name = "SwissLocation")
  public static class SwissLocation {

    @Schema(description = "SwissCanton the location is in")
    private SwissCanton canton;

    @Schema(accessMode = AccessMode.READ_ONLY)
    private Canton cantonInformation;

    private DistrictModel district;

    private LocalityMunicipalityModel localityMunicipality;

  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @SuperBuilder
  @Schema(name = "Canton")
  public static class Canton {

    @Schema(description = "Canton number, offical Number of FSO", example = "2")
    private Integer fsoNumber;

    @Schema(description = "Canton name", example = "Bern")
    private String name;

    @Schema(description = "Canton abbreviation", example = "BE")
    private String abbreviation;

  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @SuperBuilder
  @Schema(name = "District")
  public static class DistrictModel {

    @Schema(description = "SwissDistrictNumber the location is in, based on FSO", example = "242")
    private Integer fsoNumber;

    @Schema(description = "SwissDistrictName the location is in", example = "Biel/Bienne")
    @Size(max = AtlasFieldLengths.LENGTH_255)
    private String districtName;

  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @SuperBuilder
  @FieldNameConstants
  @Schema(name = "LocalityMunicipality")
  public static class LocalityMunicipalityModel {

    @Schema(description = "SwissMunicipalityNumber the location is in, , based on FSO", example = "371")
    private Integer fsoNumber;

    @Schema(description = "SwissMunicipalityName the location is in", example = "Biel/Bienne")
    @Size(max = AtlasFieldLengths.LENGTH_255)
    private String municipalityName;

    @Size(max = AtlasFieldLengths.LENGTH_255)
    @Schema(description = "SwissLocalityName the location is in", example = "Biel/Bienne")
    private String localityName;

  }

}
