package ch.sbb.prm.directory.repository;

import ch.sbb.prm.directory.entity.InformationDeskVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InformationDeskRepository extends JpaRepository<InformationDeskVersion, String>,
    JpaSpecificationExecutor<InformationDeskVersion> {

}
