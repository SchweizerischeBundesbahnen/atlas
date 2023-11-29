package ch.sbb.prm.directory.repository;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.entity.RelationVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationRepository extends JpaRepository<RelationVersion, Long>,
    JpaSpecificationExecutor<RelationVersion> {

  List<RelationVersion> findAllBySloid(String sloid);
  List<RelationVersion> findAllBySloidAndReferencePointElementType(String sloid, ReferencePointElementType referencePointType) ;
  List<RelationVersion> findAllByParentServicePointSloid(String parentServicePointSloid) ;
  List<RelationVersion> findAllByParentServicePointSloidAndReferencePointElementType(String parentServicePointSloid, ReferencePointElementType referencePointType) ;

  List<RelationVersion> findAllBySloidOrderByValidFrom(String number);

  @Modifying(clearAutomatically = true)
  @Query("update relation_version v set v.version = (v.version + 1) where v.sloid = :sloid")
  void incrementVersion(@Param("sloid") String sloid);
}
