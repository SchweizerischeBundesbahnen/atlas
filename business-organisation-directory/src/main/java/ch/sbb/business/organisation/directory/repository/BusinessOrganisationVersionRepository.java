package ch.sbb.business.organisation.directory.repository;

import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessOrganisationVersionRepository extends JpaRepository<BusinessOrganisationVersion, Long> {

  List<BusinessOrganisationVersion> findAllBySboidOrderByValidFrom(String slnid);

}
