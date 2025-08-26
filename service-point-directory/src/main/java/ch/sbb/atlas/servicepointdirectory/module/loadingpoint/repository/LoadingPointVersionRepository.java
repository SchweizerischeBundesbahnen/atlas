package ch.sbb.atlas.servicepointdirectory.module.loadingpoint.repository;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.module.loadingpoint.entity.LoadingPointVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoadingPointVersionRepository extends
    JpaRepository<LoadingPointVersion, Long>, JpaSpecificationExecutor<LoadingPointVersion> {

  List<LoadingPointVersion> findAllByServicePointNumberAndNumberOrderByValidFrom(ServicePointNumber servicePointNumber,
      Integer loadingPointNumber);

  boolean existsByServicePointNumberAndNumber(ServicePointNumber servicePointNumber, Integer loadingPointNumber);

  @Modifying(clearAutomatically = true)
  @Query("update loading_point_version v set v.version = (v.version + 1) where v.number = :number and v.servicePointNumber = "
      + ":servicePointNumber")
  void incrementVersion(ServicePointNumber servicePointNumber, Integer number);
}
