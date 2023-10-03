package ch.sbb.prm.directory.repository;

import ch.sbb.prm.directory.entity.ToiletVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ToiletRepository extends JpaRepository<ToiletVersion, String>,
    JpaSpecificationExecutor<ToiletVersion> {

}
