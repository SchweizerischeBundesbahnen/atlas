package ch.sbb.importservice.repository;

import ch.sbb.importservice.entity.BulkImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BulkImportRepository extends JpaRepository<BulkImport, Long> {

}
