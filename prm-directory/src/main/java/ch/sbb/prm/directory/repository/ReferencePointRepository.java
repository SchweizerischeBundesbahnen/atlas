package ch.sbb.prm.directory.repository;

import ch.sbb.prm.directory.entity.ReferencePointVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferencePointRepository extends JpaRepository<ReferencePointVersion, String>,
    JpaSpecificationExecutor<ReferencePointVersion> {

}
