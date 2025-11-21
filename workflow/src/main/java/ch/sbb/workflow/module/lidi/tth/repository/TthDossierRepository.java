package ch.sbb.workflow.module.lidi.tth.repository;

import ch.sbb.workflow.module.lidi.tth.entity.TthDossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TthDossierRepository extends JpaRepository<TthDossier, Long> {

}
