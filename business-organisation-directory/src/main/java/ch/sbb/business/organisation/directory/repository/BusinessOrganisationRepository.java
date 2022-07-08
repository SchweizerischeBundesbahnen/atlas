package ch.sbb.business.organisation.directory.repository;

import ch.sbb.business.organisation.directory.entity.BusinessOrganisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessOrganisationRepository extends
    JpaRepository<BusinessOrganisation, String>, JpaSpecificationExecutor<BusinessOrganisation> {

  BusinessOrganisation findBySboid(String sboid);

}
