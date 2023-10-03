package ch.sbb.prm.directory.repository;

import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationRepository extends JpaRepository<RelationVersion, String>,
    JpaSpecificationExecutor<RelationVersion> {

  List<RelationVersion> findAllBySloid(String sloid);
  List<RelationVersion> findAllBySloidAndReferencePointElementType(String sloid, ReferencePointElementType referencePointType) ;
  List<RelationVersion> findAllByParentServicePointSloid(String parentServicePointSloid) ;
  List<RelationVersion> findAllByParentServicePointSloidAndReferencePointElementType(String parentServicePointSloid, ReferencePointElementType referencePointType) ;

}
