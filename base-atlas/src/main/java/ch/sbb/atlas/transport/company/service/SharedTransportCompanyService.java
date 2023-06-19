package ch.sbb.atlas.transport.company.service;

import ch.sbb.atlas.kafka.model.transport.company.SharedTransportCompanyModel;
import ch.sbb.atlas.transport.company.repository.TransportCompanySharingDataAccessor;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SharedTransportCompanyService {

  private final TransportCompanySharingDataAccessor transportCompanySharingDataAccessor;

  public Optional<SharedTransportCompanyModel> findById(Long id) {
    return transportCompanySharingDataAccessor.findTransportCompanyById(id);
  }
}
