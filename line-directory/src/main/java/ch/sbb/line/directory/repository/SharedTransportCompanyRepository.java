package ch.sbb.line.directory.repository;

import ch.sbb.atlas.kafka.model.transport.company.SharedTransportCompanyModel;
import ch.sbb.atlas.transport.company.repository.TransportCompanySharingDataAccessor;
import ch.sbb.line.directory.entity.SharedTransportCompany;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedTransportCompanyRepository extends JpaRepository<SharedTransportCompany, Long>,
    TransportCompanySharingDataAccessor {

  default void save(SharedTransportCompanyModel model) {
    save(toEntity(model));
  }

  static SharedTransportCompany toEntity(SharedTransportCompanyModel model) {
    SharedTransportCompany sharedTransportCompany = new SharedTransportCompany();
    sharedTransportCompany.setPropertiesFromModel(model);
    return sharedTransportCompany;
  }

  default Optional<SharedTransportCompanyModel> findTransportCompanyById(Long id) {
    return findById(id).map(SharedTransportCompanyRepository::toModel);
  }

  private static SharedTransportCompanyModel toModel(SharedTransportCompany entity) {
    return entity.toModel();
  }
}
