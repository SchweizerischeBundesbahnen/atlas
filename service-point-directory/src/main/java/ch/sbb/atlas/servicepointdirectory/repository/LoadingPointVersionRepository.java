package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LoadingPointVersionRepository extends
    JpaRepository<LoadingPointVersion, Long>, JpaSpecificationExecutor<LoadingPointVersion> {

  List<LoadingPointVersion> findAllByServicePointNumberAndNumberOrderByValidFrom(ServicePointNumber servicePointNumber,
      Integer loadingPointNumber);
}
