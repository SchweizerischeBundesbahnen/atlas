package ch.sbb.timetable.hearing.repository;

import ch.sbb.timetable.hearing.entity.DossierQuestion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DossierQuestionRepository extends JpaRepository<DossierQuestion, Long> {

  @Query("""
        select q from tth_dossier_question q
        join fetch q.dossier d
        where q.id = :id
      """)
  Optional<DossierQuestion> findByIdWithDossier(@Param("id") Long id);

}
