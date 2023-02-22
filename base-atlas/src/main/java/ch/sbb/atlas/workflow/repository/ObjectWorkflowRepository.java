package ch.sbb.atlas.workflow.repository;

import ch.sbb.atlas.workflow.model.BaseWorkflowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface ObjectWorkflowRepository<T extends BaseWorkflowEntity> extends JpaRepository<T, Long> {

    Optional<T> findByWorkflowId(Long workflowId);
}
