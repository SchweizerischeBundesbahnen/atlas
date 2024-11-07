package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.Line;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LineRepository extends JpaRepository<Line, String>,
    JpaSpecificationExecutor<Line> {

  Optional<Line> findAllBySlnid(String slnid);

  @Query("SELECT l FROM overview_line_subline as l"
      + " JOIN coverage as c "
      + " ON l.slnid = c.slnid"
      + " WHERE c.modelType = ch.sbb.atlas.api.lidi.enumaration.ModelType.LINE"
      + " AND c.coverageType = ch.sbb.atlas.api.lidi.enumaration.CoverageType.COMPLETE")
  List<Line> getAllCoveredLines();
}
