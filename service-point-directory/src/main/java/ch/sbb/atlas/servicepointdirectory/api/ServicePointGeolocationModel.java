package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.enumeration.SwissCanton;
import ch.sbb.atlas.servicepointdirectory.model.CoordinatePair;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.util.Map;
import javax.validation.constraints.Size;
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

  @Schema(description = "Country")
  private Country country;

  @JsonInclude
  @Schema(description = "ISO 2 abbreviation of the country, based on coordinates", example = "CH", accessMode = AccessMode.READ_ONLY)
  public String getIsoCountryCode() {
    return getCountry().getIsoCode();
  }

  @Schema(description = "SwissCanton the location is in")
  private SwissCanton swissCanton;

  @Schema(accessMode = AccessMode.READ_ONLY)
  private Canton swissCantonInformation;

  private DistrictModel district;

  private LocalityMunicipalityModel localityMunicipality;

  public static ServicePointGeolocationModel fromEntity(ServicePointGeolocation servicePointGeolocation) {
    Map<SpatialReference, CoordinatePair> coordinates = getTransformedCoordinates(servicePointGeolocation);
    return ServicePointGeolocationModel.builder()
        .country(servicePointGeolocation.getCountry())
        .swissCanton(servicePointGeolocation.getSwissCanton())
        .swissCantonInformation(Canton.builder()
            .abbreviation(servicePointGeolocation.getSwissCanton().getAbbreviation())
            .number(servicePointGeolocation.getSwissCanton().getNumber())
            .name(servicePointGeolocation.getSwissCanton().getName())
            .build())
        .district(DistrictModel.builder()
            .swissDistrictNumber(servicePointGeolocation.getSwissDistrictNumber())
            .swissDistrictName(servicePointGeolocation.getSwissDistrictName())
            .build())
        .localityMunicipality(LocalityMunicipalityModel.builder()
            .swissMunicipalityNumber(servicePointGeolocation.getSwissMunicipalityNumber())
            .swissMunicipalityName(servicePointGeolocation.getSwissMunicipalityName())
            .swissLocalityName(servicePointGeolocation.getSwissLocalityName())
            .build())
        .lv95(coordinates.get(SpatialReference.LV95))
        .wgs84(coordinates.get(SpatialReference.WGS84))
        .wgs84web(coordinates.get(SpatialReference.WGS84WEB))
        .height(servicePointGeolocation.getHeight())
        .build();
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @SuperBuilder
  @Schema(name = "Canton")
  public static class Canton {

    @Schema(description = "Canton number", example = "1")
    private Integer number;

    @Schema(description = "Canton name", example = "Zürich")
    private String name;

    @Schema(description = "Canton abbreviation", example = "ZH")
    private String abbreviation;

  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @SuperBuilder
  @Schema(name = "District")
  public static class DistrictModel {

    @Schema(description = "SwissDistrictNumber the location is in", example = "246")
    private Integer swissDistrictNumber;

    @Schema(description = "SwissDistrictName the location is in", example = "Bern-Mittelland")
    @Size(max = AtlasFieldLengths.LENGTH_255)
    private String swissDistrictName;

  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @SuperBuilder
  @FieldNameConstants
  @Schema(name = "LocalityMunicipality")
  public static class LocalityMunicipalityModel {

    @Schema(description = "SwissMunicipalityNumber the location is in", example = "351")
    private Integer swissMunicipalityNumber;

    @Schema(description = "SwissMunicipalityName the location is in", example = "Bern")
    @Size(max = AtlasFieldLengths.LENGTH_255)
    private String swissMunicipalityName;

    @Size(max = AtlasFieldLengths.LENGTH_255)
    @Schema(description = "SwissLocalityName the location is in", example = "Bern")
    private String swissLocalityName;

  }

}
