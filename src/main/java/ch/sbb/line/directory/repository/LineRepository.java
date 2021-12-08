package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.Line;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineRepository extends JpaRepository<Line, String> {

  Page<Line> findAllBySwissLineNumberLike(Pageable pageable, String swissLineNumber);

  Optional<Line> findAllBySlnid(String slnid);
}
