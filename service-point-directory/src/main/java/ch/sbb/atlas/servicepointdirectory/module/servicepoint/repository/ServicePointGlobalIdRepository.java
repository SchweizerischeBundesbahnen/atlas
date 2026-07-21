package ch.sbb.atlas.servicepointdirectory.module.servicepoint.repository;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointGlobalId;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePointGlobalIdRepository extends JpaRepository<ServicePointGlobalId, Long> {

  Optional<ServicePointGlobalId> findByServicePointNumber(ServicePointNumber servicePointNumber);

  List<ServicePointGlobalId> findByServicePointNumberIn(Collection<ServicePointNumber> servicePointNumbers);

  Optional<ServicePointGlobalId> findByGlobalId(String globalId);

}
