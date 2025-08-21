package ch.sbb.business.organisation.directory.module.businessorganisation.repository;

import ch.sbb.business.organisation.directory.module.businessorganisation.entity.BusinessOrganisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessOrganisationRepository extends
    JpaRepository<BusinessOrganisation, String>, JpaSpecificationExecutor<BusinessOrganisation> {

  BusinessOrganisation findBySboid(String sboid);

}
