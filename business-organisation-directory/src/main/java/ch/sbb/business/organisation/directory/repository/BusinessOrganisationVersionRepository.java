package ch.sbb.business.organisation.directory.repository;

import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessOrganisationVersionRepository extends
    JpaRepository<BusinessOrganisationVersion, Long> {

  List<BusinessOrganisationVersion> findAllBySboidOrderByValidFrom(String slnid);

  default List<BusinessOrganisationVersion> findAbbreviationOverlaps(
      BusinessOrganisationVersion version) {
    return Stream.of(
                     findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationDeIgnoreCase(
                         version.getValidFrom(), version.getValidTo(), version.getAbbreviationDe())
                         .stream(),
                     findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationFrIgnoreCase(
                         version.getValidFrom(), version.getValidTo(), version.getAbbreviationFr())
                         .stream(),
                     findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationItIgnoreCase(
                         version.getValidFrom(), version.getValidTo(), version.getAbbreviationIt())
                         .stream(),
                     findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationEnIgnoreCase(
                         version.getValidFrom(), version.getValidTo(), version.getAbbreviationEn())
                         .stream())
                 .flatMap(Function.identity())
                 .filter(i -> !i.getSboid().equals(version.getSboid()))
                 .distinct()
                 .collect(Collectors.toList());
  }

  List<BusinessOrganisationVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationDeIgnoreCase(
      LocalDate validFrom, LocalDate validTo, String abbreviation);

  List<BusinessOrganisationVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationFrIgnoreCase(
      LocalDate validFrom, LocalDate validTo, String abbreviation);

  List<BusinessOrganisationVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationItIgnoreCase(
      LocalDate validFrom, LocalDate validTo, String abbreviation);

  List<BusinessOrganisationVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndAbbreviationEnIgnoreCase(
      LocalDate validFrom, LocalDate validTo, String abbreviation);

}
