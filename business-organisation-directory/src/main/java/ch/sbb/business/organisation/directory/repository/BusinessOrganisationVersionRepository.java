package ch.sbb.business.organisation.directory.repository;

import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BusinessOrganisationVersionRepository extends
    JpaRepository<BusinessOrganisationVersion, Long>,
    JpaSpecificationExecutor<BusinessOrganisationVersion> {

  List<BusinessOrganisationVersion> findAllBySboidOrderByValidFrom(String slnid);

  List<BusinessOrganisationVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationDe(
      LocalDate validFrom, LocalDate validTo, String abbreviation);

  List<BusinessOrganisationVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationFr(
      LocalDate validFrom, LocalDate validTo, String abbreviation);

  List<BusinessOrganisationVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationIt(
      LocalDate validFrom, LocalDate validTo, String abbreviation);

  List<BusinessOrganisationVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationEn(
      LocalDate validFrom, LocalDate validTo, String abbreviation);

  List<BusinessOrganisationVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndOrganisationNumber(
      LocalDate validFrom, LocalDate validTo, Integer organisationNumber);

  @Modifying(clearAutomatically = true)
  @Query("update business_organisation_version v set v.version = (v.version + 1) where v.sboid = :sboid")
  void incrementVersion(@Param("sboid") String sboid);

}
