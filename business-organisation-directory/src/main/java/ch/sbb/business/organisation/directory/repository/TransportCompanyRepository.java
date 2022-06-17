package ch.sbb.business.organisation.directory.repository;

import ch.sbb.business.organisation.directory.entity.TransportCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportCompanyRepository extends JpaRepository<TransportCompany, Long>,
    JpaSpecificationExecutor<TransportCompany> {

}
