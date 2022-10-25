package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.LineVersionWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineVersionWorkflowRepository extends JpaRepository<LineVersionWorkflow, Long> {

}
