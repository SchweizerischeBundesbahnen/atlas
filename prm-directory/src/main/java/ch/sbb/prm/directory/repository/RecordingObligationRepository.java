package ch.sbb.prm.directory.repository;

import ch.sbb.prm.directory.entity.RecordingObligation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordingObligationRepository extends JpaRepository<RecordingObligation, String> {

    List<RecordingObligation> findAllBySloidIn(List<String> sloids);

}
