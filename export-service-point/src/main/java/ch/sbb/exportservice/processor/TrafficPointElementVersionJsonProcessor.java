package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseModel;
import ch.sbb.atlas.api.servicepoint.ReadTrafficPointElementVersionModel;
import ch.sbb.exportservice.entity.TrafficPointElementVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class TrafficPointElementVersionJsonProcessor extends BaseServicePointProcessor implements ItemProcessor<TrafficPointElementVersion,
        ReadTrafficPointElementVersionModel> {

    @Override
    public ReadTrafficPointElementVersionModel process(TrafficPointElementVersion version) {
        GeolocationBaseModel geolocation = toModel(version.getTrafficPointElementGeolocation());

        return ReadTrafficPointElementVersionModel.builder()
                .designation(version.getDesignation())
                .designationOperational(version.getDesignationOperational())
                .length(version.getLength())
                .trafficPointElementType(version.getTrafficPointElementType())
                .boardingAreaHeight(version.getBoardingAreaHeight())
                .compassDirection(version.getCompassDirection())
                .parentSloid(version.getParentSloid())
                .trafficPointElementGeolocation(geolocation)
                .servicePointNumber(version.getServicePointNumber())
                .build();
    }

}
