package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.enumaration.CoverageType;
import ch.sbb.line.directory.enumaration.ModelType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LineRepository extends JpaRepository<Line, String>,
    JpaSpecificationExecutor<Line> {

  Optional<Line> findAllBySlnid(String slnid);

  @Query("SELECT l FROM line as l"
      + " JOIN coverage as c "
      + " ON l.slnid = c.slnid"
      + " WHERE c.modelType = ch.sbb.line.directory.enumaration.ModelType.LINE"
      + " AND c.coverageType = ch.sbb.line.directory.enumaration.CoverageType.COMPLETE")
  List<Line> getAllCoveredLines();
}
