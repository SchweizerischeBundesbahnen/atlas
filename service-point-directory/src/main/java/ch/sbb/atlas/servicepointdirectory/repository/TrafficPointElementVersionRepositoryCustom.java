package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementRequestParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface TrafficPointElementVersionRepositoryCustom {

    Page<TrafficPointElementVersion> blaBloBlu2(TrafficPointElementRequestParams trafficPointElementRequestParams, Pageable pageable);

}
