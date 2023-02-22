package ch.sbb.atlas.servicepointdirectory.model;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeoData;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.enumeration.SwissCanton;
import java.time.LocalDate;

public class TestData {

  public static ServicePointGeoData testGeoDataLv95() {
    return ServicePointGeoData.builder()
        .id(1L)
        .number(85070003)
        .spatialReference(SpatialReference.LV95)
        .east(2674198D)
        .north(1244494D)
        .height(100.2)
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.BERN)
        .swissDistrictName("Bern")
        .swissDistrictNumber(5)
        .swissMunicipalityNumber(5)
        .swissMunicipalityName("Bern")
        .swissLocalityName("Bern")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
  }

  public static ServicePointGeoData testGeoDataWgs84Web() {
    return ServicePointGeoData.builder()
        .id(1L)
        .number(85070003)
        .spatialReference(SpatialReference.WGS84WEB)
        .east(0.1D)
        .north(0.1D)
        .height(100.2)
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.BERN)
        .swissDistrictName("Bern")
        .swissDistrictNumber(5)
        .swissMunicipalityNumber(5)
        .swissMunicipalityName("Bern")
        .swissLocalityName("Bern")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
  }

  public static ServicePointGeolocation testGeolocationWgs84() {
    return ServicePointGeolocation.builder()
                                  .spatialReference(SpatialReference.WGS84)
                                  .east(0.1D)
                                  .north(0.1D)
                                  .height(100.2)
                                  .country(Country.SWITZERLAND)
                                  .swissCanton(SwissCanton.BERN)
                                  .swissDistrictName("Bern")
                                  .swissDistrictNumber(5)
                                  .swissMunicipalityNumber(5)
                                  .swissMunicipalityName("Bern")
                                  .swissLocalityName("Bern")
                                  .build();
  }

  public static ServicePointVersion testServicePoint() {
    return ServicePointVersion.builder()
                              .number(ServicePointNumber.of(85070003))
                              .numberShort(1)
                              .country(Country.SWITZERLAND)
                              .designationLong("long designation")
                              .designationOfficial("official designation")
                              .abbreviation("BE")
                              .statusDidok3(ServicePointStatus.from(1))
                              .businessOrganisation("somesboid")
                              .status(Status.VALIDATED)
                              .validFrom(LocalDate.of(2020, 1, 1))
                              .validTo(LocalDate.of(2020, 12, 31))
                              .build();
  }
}
