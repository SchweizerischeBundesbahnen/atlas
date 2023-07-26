package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementRequestParams;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrafficPointElementVersionRepositoryCustom {

    List<TrafficPointElementVersion> blaBloBlu2(TrafficPointElementRequestParams trafficPointElementRequestParams);

}
