package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.Line;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LineRepository extends JpaRepository<Line, String>,
    JpaSpecificationExecutor<Line> {

  Optional<Line> findAllBySlnid(String slnid);
}
