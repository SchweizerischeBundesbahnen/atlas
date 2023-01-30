package ch.sbb.importservice.repository;

import ch.sbb.importservice.entitiy.ImportProcessItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ImportProcessedItemRepository extends JpaRepository<ImportProcessItem, Long> {

  void deleteAllByStepExecutionId(Long stepExecutionId);

  List<ImportProcessItem> findAllByStepExecutionId(Long stepExecutionId);
}
