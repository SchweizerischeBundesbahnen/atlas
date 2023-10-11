package ch.sbb.prm.directory.repository;

import ch.sbb.prm.directory.entity.ServicePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePointRepository extends JpaRepository<ServicePoint, String> {

}
