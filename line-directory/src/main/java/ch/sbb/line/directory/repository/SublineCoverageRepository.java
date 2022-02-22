package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.SublineCoverage;
import ch.sbb.line.directory.enumaration.ModelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SublineCoverageRepository extends JpaRepository<SublineCoverage, Long> {

  SublineCoverage findSublineCoverageBySlnid(String slnId);

  SublineCoverage findSublineCoverageBySlnidAndModelType(String slnId, ModelType modelType);
}
