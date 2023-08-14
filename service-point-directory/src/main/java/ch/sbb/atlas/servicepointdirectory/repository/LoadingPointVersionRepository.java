package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoadingPointVersionRepository extends
    JpaRepository<LoadingPointVersion, Long>, JpaSpecificationExecutor<LoadingPointVersion> {

  List<LoadingPointVersion> findAllByServicePointNumberAndNumberOrderByValidFrom(ServicePointNumber servicePointNumber,
      Integer loadingPointNumber);
}
