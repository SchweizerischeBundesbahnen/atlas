package ch.sbb.atlas.servicepointdirectory.mapper;

import static ch.sbb.atlas.servicepointdirectory.mapper.GeolocationMapper.getTransformedCoordinates;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointGeolocationModel;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointGeolocationModel.Canton;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointGeolocationModel.DistrictModel;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointGeolocationModel.LocalityMunicipalityModel;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointGeolocationModel.SwissLocation;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.model.CoordinatePair;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicePointGeolocationMapper {

    public static ServicePointGeolocationModel toModel(ServicePointGeolocation servicePointGeolocation) {
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

    public static ServicePointGeolocation toEntity(ServicePointGeolocationModel servicePointGeolocationModel) {
        return ServicePointGeolocation.builder()
            .country(servicePointGeolocationModel.getCountry())
            .swissCanton(servicePointGeolocationModel.getSwissLocation().getCanton())
            .swissDistrictName(servicePointGeolocationModel.getSwissLocation().getDistrict().getDistrictName())
            .swissDistrictNumber(servicePointGeolocationModel.getSwissLocation().getDistrict().getFsoNumber())
            .swissMunicipalityNumber(servicePointGeolocationModel.getSwissLocation().getLocalityMunicipality().getFsoNumber())
            .swissMunicipalityName(servicePointGeolocationModel.getSwissLocation().getLocalityMunicipality().getMunicipalityName())
            .swissLocalityName(servicePointGeolocationModel.getSwissLocation().getLocalityMunicipality().getLocalityName())
            .spatialReference(servicePointGeolocationModel.getSpatialReference())
            .north(servicePointGeolocationModel.getLv95().getNorth())
            .east(servicePointGeolocationModel.getLv95().getEast())
            .north(servicePointGeolocationModel.getWgs84().getNorth())
            .east(servicePointGeolocationModel.getWgs84().getEast())
            .north(servicePointGeolocationModel.getWgs84web().getNorth())
            .east(servicePointGeolocationModel.getWgs84web().getEast())
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
}
