package ch.sbb.business.organisation.directory.repository;

import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportCompanyRelationRepository extends JpaRepository<TransportCompanyRelation, Integer> {

}
