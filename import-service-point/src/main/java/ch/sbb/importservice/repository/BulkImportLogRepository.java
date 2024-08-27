package ch.sbb.importservice.repository;

import ch.sbb.importservice.entity.BulkImportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface BulkImportLogRepository extends JpaRepository<BulkImportLog, Long> {

}
