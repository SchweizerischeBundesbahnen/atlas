package ch.sbb.importservice.repository;

import ch.sbb.importservice.entitiy.ImportProcessItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportProcessedItemRepository extends JpaRepository<ImportProcessItem, Long> {

  void deleteAllByStepExecutionId(Long stepExecutionId);

}
