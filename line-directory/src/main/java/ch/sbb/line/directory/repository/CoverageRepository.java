package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.SublineCoverage;
import ch.sbb.line.directory.enumaration.ModelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoverageRepository extends JpaRepository<SublineCoverage, Long> {

  SublineCoverage findSublineCoverageBySlnidAndModelType(String slnId, ModelType modelType);
}
