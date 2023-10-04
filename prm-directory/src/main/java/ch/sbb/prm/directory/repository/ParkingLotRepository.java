package ch.sbb.prm.directory.repository;

import ch.sbb.prm.directory.entity.ParkingLotVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLotVersion, String>,
    JpaSpecificationExecutor<ParkingLotVersion> {

  List<ParkingLotVersion> findByParentServicePointSloid(String parentServicePointSloid);

}
