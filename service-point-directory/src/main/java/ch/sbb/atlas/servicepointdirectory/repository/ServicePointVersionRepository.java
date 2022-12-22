package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePointVersionRepository extends
    JpaRepository<ServicePointVersion, Long> {

  List<ServicePointVersion> findAllByNumber(Integer number);

}
