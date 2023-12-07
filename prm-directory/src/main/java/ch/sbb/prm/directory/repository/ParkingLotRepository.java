package ch.sbb.prm.directory.repository;

import ch.sbb.prm.directory.entity.ParkingLotVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLotVersion, Long>,
    JpaSpecificationExecutor<ParkingLotVersion> {

  List<ParkingLotVersion> findByParentServicePointSloid(String parentServicePointSloid);

  List<ParkingLotVersion> findAllBySloidOrderByValidFrom(String sloid);

  @Modifying(clearAutomatically = true)
  @Query("update parking_lot_version v set v.version = (v.version + 1) where v.sloid = :sloid")
  void incrementVersion(@Param("sloid") String sloid);
}
