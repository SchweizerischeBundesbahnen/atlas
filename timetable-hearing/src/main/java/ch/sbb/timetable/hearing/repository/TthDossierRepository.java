package ch.sbb.timetable.hearing.repository;

import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.timetable.hearing.entity.TthDossier;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TthDossierRepository extends JpaRepository<TthDossier, Long>, JpaSpecificationExecutor<TthDossier> {

  @Query("""
      select sIds from ch.sbb.timetable.hearing.entity.TthDossier tthd
      join tthd.statementIds sIds
      where tthd.dossierStatus in :dossierStatus""")
  List<Long> findStatementIdsByDossierStatusIn(Collection<DossierStatus> dossierStatus);

  @Transactional
  @Modifying
  @Query("""
      update ch.sbb.timetable.hearing.entity.TthDossier tthd
      set tthd.dossierStatus = :statusToSet
      where tthd.dossierStatus in :statusToChange""")
  void updateDossierStatus(DossierStatus statusToSet, Collection<DossierStatus> statusToChange);
}