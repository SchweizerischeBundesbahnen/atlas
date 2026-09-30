package ch.sbb.line.directory.module.line.repository;

import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface LineVersionV3Repository
    extends JpaRepository<LineVersionV3, Long>, JpaSpecificationExecutor<LineVersionV3> {

  List<LineVersionV3> findAllBySlnidOrderByValidFrom(String slnid);

  List<LineVersionV3> findAllBySlnidIn(Set<String> slnids);

}
