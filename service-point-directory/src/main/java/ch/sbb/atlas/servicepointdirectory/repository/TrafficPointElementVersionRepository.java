package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrafficPointElementVersionRepository extends
    PagingAndSortingRepository<TrafficPointElementVersion, Long> {

  List<TrafficPointElementVersion> findAllByServicePointNumber(Integer servicePointNumber);

}
