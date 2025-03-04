package ch.sbb.importservice.repository;

import ch.sbb.importservice.entity.GeoUpdateProcessItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface GeoUpdateProcessItemRepository extends JpaRepository<GeoUpdateProcessItem, Long> {

  void deleteAllByStepExecutionId(Long stepExecutionId);

  List<GeoUpdateProcessItem> findAllByStepExecutionId(Long stepExecutionId);
}
