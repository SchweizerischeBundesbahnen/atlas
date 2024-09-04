package ch.sbb.atlas.servicepointdirectory.controller;

import static ch.sbb.atlas.servicepointdirectory.ServicePointTestData.getServicePointGeolocationBernMittelland;

import ch.sbb.atlas.api.servicepoint.DistrictModel;
import ch.sbb.atlas.api.servicepoint.LocalityMunicipalityModel;
import ch.sbb.atlas.api.servicepoint.ServicePointGeolocationReadModel;
import ch.sbb.atlas.api.servicepoint.SwissLocation;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.geodata.mapper.UpdateGeoLocationResultContainer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UpdateGeoLocationTesData {

  public UpdateGeoLocationResultContainer getModel() {
    ServicePointGeolocation currentServicePointGeolocation = getServicePointGeolocationBernMittelland();
    ServicePointGeolocation updatedServicePointGeolocation = getServicePointGeolocationBernMittelland();
    updatedServicePointGeolocation.setSwissLocalityName("Wyleregg");
    updatedServicePointGeolocation.setSwissMunicipalityName("Wyleregg");
    updatedServicePointGeolocation.setSwissMunicipalityNumber(101);

    return UpdateGeoLocationResultContainer.builder()
        .currentServicePointGeolocation(currentServicePointGeolocation)
        .updatedServicePointGeolocation(updatedServicePointGeolocation)
        .sloid("ch:1:sloid:7000")
        .id(1000L)
        .build();
  }

  public static ServicePointGeolocationReadModel getServicePointGeolocationReadModel() {
    LocalityMunicipalityModel municipality = LocalityMunicipalityModel.builder()
        .municipalityName("Bern")
        .localityName("Bern")
        .fsoNumber(236).build();
    DistrictModel district = DistrictModel.builder().districtName("Bern-Mittelland").fsoNumber(632).build();
    return ServicePointGeolocationReadModel.builder()
        .swissLocation(
            SwissLocation.builder()
                .canton(SwissCanton.BERN)
                .localityMunicipality(municipality)
                .district(district)
                .build())
        .build();
  }

}
