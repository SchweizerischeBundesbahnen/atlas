package ch.sbb.business.organisation.directory.repository;

import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportCompanyRelationRepository extends
    JpaRepository<TransportCompanyRelation, Long> {

  List<TransportCompanyRelation> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndSboid(
      LocalDate validFrom, LocalDate validTo, String sboid);
}
