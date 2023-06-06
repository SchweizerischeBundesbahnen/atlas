package ch.sbb.exportservice.repository;

import ch.sbb.exportservice.entity.ServicePointVersion;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Primary
@Component
public interface PointRepository extends JpaRepository<ServicePointVersion, Long> {

}
