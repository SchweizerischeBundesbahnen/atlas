package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.Subline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SublineRepository extends JpaRepository<Subline, String> {

}
