package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.LineVersionSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LineVersionSnapshotRepository extends JpaRepository<LineVersionSnapshot, Long>,
    JpaSpecificationExecutor<LineVersionSnapshot> {

}
