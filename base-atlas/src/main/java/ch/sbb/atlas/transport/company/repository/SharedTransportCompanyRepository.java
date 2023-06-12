package ch.sbb.atlas.transport.company.repository;

import ch.sbb.atlas.transport.company.entity.SharedTransportCompany;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedTransportCompanyRepository extends JpaRepository<SharedTransportCompany, Long> {

}
