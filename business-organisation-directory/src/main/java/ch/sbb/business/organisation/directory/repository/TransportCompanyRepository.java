package ch.sbb.business.organisation.directory.repository;

import ch.sbb.business.organisation.directory.entity.TransportCompany;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportCompanyRepository extends JpaRepository<TransportCompany, Long>,
    JpaSpecificationExecutor<TransportCompany> {

  @Query("select tc from transport_company_relation r join transport_company tc "
      + "on r.transportCompany=tc "
      + "where "
      + "(r.validTo >= current_date and r.validFrom <= current_date) and "
      + "(tc.transportCompanyStatus in "
      + "(ch.sbb.atlas.api.bodi.enumeration.TransportCompanyStatus.INACTIVE, "
      + "ch.sbb.atlas.api.bodi.enumeration.TransportCompanyStatus.LIQUIDATED))")
  List<TransportCompany> findTransportCompaniesWithInvalidRelations();

  @Query("select tc from transport_company tc "
      + "join transport_company_relation tr "
      + "on tr.transportCompany=tc "
      + "where tr.sboid=:sboid")
  List<TransportCompany> findAllWithSboid(String sboid);
}
