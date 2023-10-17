package ch.sbb.prm.directory.repository;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StopPlaceRepository extends JpaRepository<StopPlaceVersion, Long>,
    JpaSpecificationExecutor<StopPlaceVersion> {

    boolean existsBySloid(String sloid);

    @Modifying(clearAutomatically = true)
    @Query("update stop_place_version v set v.version = (v.version + 1) where v.number = :number")
    void incrementVersion(@Param("number") ServicePointNumber number);

    List<StopPlaceVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number);
}
