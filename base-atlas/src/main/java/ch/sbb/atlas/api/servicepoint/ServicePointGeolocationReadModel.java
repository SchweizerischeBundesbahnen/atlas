package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.servicepoint.Country;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
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
@Schema(description = "Service point geolocation",
        name = "ServicePointGeolocation",
        example = """
            {"spatialReference" : "LV95",
                "lv95" : {
                  "north" : 1201099.0,
                  "east" : 2600783.0
                },
                "wgs84" : {
                  "north" : 46.96096808019183,
                  "east" : 7.448919722210132
                },
                "wgs84web" : {
                  "north" : 0,
                  "east" : 0
                },
                "height" : 555.0,
                "swissLocation" : {
                  "canton" : "BERN",
                  "cantonInformation" : {
                    "fsoNumber" : 2,
                    "name" : "Bern",
                    "abbreviation" : "BE"
                  },
                  "district" : {
                    "fsoNumber" : 246,
                    "districtName" : "Bern-Mittelland"
                  },
                  "localityMunicipality" : {
                    "fsoNumber" : 351,
                    "municipalityName" : "Bern",
                    "localityName" : "Bern"
                  }
                },
                "isoCountryCode" : "CH"}""")
public class ServicePointGeolocationReadModel extends GeolocationBaseReadModel {

  @JsonIgnore
  private Country country;
  private SwissLocation swissLocation;

  @JsonInclude
  @Schema(description = "ISO 2 abbreviation of the country, based on coordinates", example = "CH", accessMode =
      AccessMode.READ_ONLY)
  public String getIsoCountryCode() {
    return getCountry() == null ? null : getCountry().getIsoCode();
  }

}