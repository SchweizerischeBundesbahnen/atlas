package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.Coverage;
import ch.sbb.atlas.api.line.enumaration.ModelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoverageRepository extends JpaRepository<Coverage, Long> {

  Coverage findSublineCoverageBySlnidAndModelType(String slnId, ModelType modelType);

  @Override
  void deleteById(Long aLong);
}
