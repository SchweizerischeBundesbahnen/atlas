package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {

}
