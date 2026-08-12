package ch.sbb.timetable.hearing.repository;

import ch.sbb.timetable.hearing.entity.TthDossierQuestion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TthDossierQuestionRepository extends JpaRepository<TthDossierQuestion, Long> {

  @Query("""
        select q from tth_dossier_question q
        join fetch q.tthDossier d
        where q.id = :id
      """)
  Optional<TthDossierQuestion> findByIdWithDossier(@Param("id") Long id);

}
