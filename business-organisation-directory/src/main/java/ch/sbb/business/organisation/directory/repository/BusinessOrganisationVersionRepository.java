package ch.sbb.business.organisation.directory.repository;

import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessOrganisationVersionRepository extends
    JpaRepository<BusinessOrganisationVersion, Long> {

  List<BusinessOrganisationVersion> findAllBySboidOrderByValidFrom(String slnid);

  List<BusinessOrganisationVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationDe(
      LocalDate validFrom, LocalDate validTo, String abbreviation);

  List<BusinessOrganisationVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationFr(
      LocalDate validFrom, LocalDate validTo, String abbreviation);

  List<BusinessOrganisationVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationIt(
      LocalDate validFrom, LocalDate validTo, String abbreviation);

  List<BusinessOrganisationVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationEn(
      LocalDate validFrom, LocalDate validTo, String abbreviation);

}
