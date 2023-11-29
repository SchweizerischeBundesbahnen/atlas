package ch.sbb.prm.directory.repository;

import ch.sbb.prm.directory.entity.PlatformVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformRepository extends JpaRepository<PlatformVersion, Long>,
    JpaSpecificationExecutor<PlatformVersion> {

  List<PlatformVersion> findByParentServicePointSloid(String parentServicePointSloid);

  @Modifying(clearAutomatically = true)
  @Query("update platform_version v set v.version = (v.version + 1) where v.sloid = :sloid")
  void incrementVersion(@Param("sloid") String sloid);

  List<PlatformVersion> findAllBySloidOrderByValidFrom(String sloid);

  boolean existsBySloid(String sloid);
}
