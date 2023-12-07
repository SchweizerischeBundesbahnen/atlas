package ch.sbb.prm.directory.repository;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.entity.StopPointVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StopPointRepository extends JpaRepository<StopPointVersion, Long>,
    JpaSpecificationExecutor<StopPointVersion> {

    boolean existsBySloid(String sloid);

    @Modifying(clearAutomatically = true)
    @Query("update stop_point_version v set v.version = (v.version + 1) where v.sloid = :sloid")
    void incrementVersion(@Param("sloid") String sloid);

    List<StopPointVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number);

    boolean existsByNumber(ServicePointNumber number);

    List<StopPointVersion> findAllBySloidOrderByValidFrom(String sloid);
}
