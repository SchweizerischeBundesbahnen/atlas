package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.api.model.GeolocationBaseModel;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.GeolocationBaseEntity;
import ch.sbb.atlas.servicepointdirectory.model.CoordinatePair;
import ch.sbb.atlas.servicepointdirectory.transformer.CoordinateTransformer;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GeolocationMapper {

    public static GeolocationBaseModel toModel(GeolocationBaseEntity geolocation) {
        if (geolocation == null) {
            return null;
        }
        Map<SpatialReference, CoordinatePair> coordinates = getTransformedCoordinates(geolocation);
        return GeolocationBaseModel.builder()
            .spatialReference(geolocation.getSpatialReference())
            .lv95(coordinates.get(SpatialReference.LV95))
            .wgs84(coordinates.get(SpatialReference.WGS84))
            .wgs84web(coordinates.get(SpatialReference.WGS84WEB))
            .height(geolocation.getHeight())
            .build();
    }

    static public Map<SpatialReference, CoordinatePair> getTransformedCoordinates(GeolocationBaseEntity entity) {
        Map<SpatialReference, CoordinatePair> coordinates = new EnumMap<>(SpatialReference.class);

        CoordinateTransformer coordinateTransformer = new CoordinateTransformer();
        Stream.of(SpatialReference.values()).forEach(spatialReference -> {
            if (spatialReference == entity.getSpatialReference()) {
                coordinates.put(spatialReference, entity.asCoordinatePair());
            } else {
                coordinates.put(spatialReference, coordinateTransformer.transform(entity.asCoordinatePair(), spatialReference));
            }
        });
        return coordinates;
    }

}
