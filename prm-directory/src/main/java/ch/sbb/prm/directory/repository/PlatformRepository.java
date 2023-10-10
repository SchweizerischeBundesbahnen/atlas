package ch.sbb.prm.directory.repository;

import ch.sbb.prm.directory.entity.PlatformVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformRepository extends JpaRepository<PlatformVersion, String>,
    JpaSpecificationExecutor<PlatformVersion> {

  List<PlatformVersion> findByParentServicePointSloid(String parentServicePointSloid);

}
