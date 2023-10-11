package ch.sbb.prm.directory.repository;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferencePointRepository extends JpaRepository<ReferencePointVersion, Long>,
    JpaSpecificationExecutor<ReferencePointVersion> {

  List<ReferencePointVersion> findByParentServicePointSloid(String parentServicePointSloid);

  List<ReferencePointVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number);
}
