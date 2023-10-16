package ch.sbb.prm.directory.repository;

import ch.sbb.prm.directory.entity.SharedServicePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedServicePointRepository extends JpaRepository<SharedServicePoint, String> {

}
