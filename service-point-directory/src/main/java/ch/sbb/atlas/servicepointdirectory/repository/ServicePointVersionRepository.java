package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePointVersionRepository extends
    PagingAndSortingRepository<ServicePointVersion, Long> {

  List<ServicePointVersion> findAllByHasGeolocation(Boolean hasGeolocation, Pageable pageable);

}
