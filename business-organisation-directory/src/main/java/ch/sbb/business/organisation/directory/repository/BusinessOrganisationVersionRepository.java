package ch.sbb.business.organisation.directory.repository;

import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessOrganisationVersionRepository extends
    JpaRepository<BusinessOrganisationVersion, Long>, JpaSpecificationExecutor<BusinessOrganisationVersion> {

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

  @Query("SELECT bov FROM business_organisation_version as bov"
      + " ORDER BY bov.sboid, bov.validFrom ASC")
  List<BusinessOrganisationVersion> getFullLineVersions();

  @Query("SELECT bov FROM business_organisation_version as bov"
      + " WHERE  :actualDate >= bov.validFrom AND :actualDate <= bov.validTo"
      + " ORDER BY bov.sboid, bov.validFrom ASC")
  List<BusinessOrganisationVersion> getActualLineVersions(LocalDate actualDate);
}
