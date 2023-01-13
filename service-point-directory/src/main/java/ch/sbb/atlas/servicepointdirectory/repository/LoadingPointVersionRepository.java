package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoadingPointVersionRepository extends
    JpaRepository<LoadingPointVersion, Long> {

  List<LoadingPointVersion> findAllByServicePointNumberAndNumberOrderByValidFrom(ServicePointNumber servicePointNumber,
      Integer loadingPointNumber);
}
