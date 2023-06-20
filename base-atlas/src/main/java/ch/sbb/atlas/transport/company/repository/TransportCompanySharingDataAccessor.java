package ch.sbb.atlas.transport.company.repository;

import ch.sbb.atlas.kafka.model.transport.company.SharedTransportCompanyModel;
import java.util.Optional;

public interface TransportCompanySharingDataAccessor {

  void save(SharedTransportCompanyModel entity);

  Optional<SharedTransportCompanyModel> findTransportCompanyById(Long id);
}
