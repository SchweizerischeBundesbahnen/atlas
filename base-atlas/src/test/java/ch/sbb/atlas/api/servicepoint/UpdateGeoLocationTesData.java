package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.kafka.model.SwissCanton;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UpdateGeoLocationTesData {


  public UpdateGeoServicePointVersionResultModel getModel(){
    ServicePointGeolocationReadModel currentServicePointGeolocation = getServicePointGeolocationReadModel();
    ServicePointGeolocationReadModel updatedServicePointGeolocation = getServicePointGeolocationReadModel();
    updatedServicePointGeolocation.getSwissLocation()
        .setLocalityMunicipality(
            LocalityMunicipalityModel.builder().municipalityName("Wyleregg").localityName("Wyleregg").fsoNumber(101).build());
    return UpdateGeoServicePointVersionResultModel.builder()
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
