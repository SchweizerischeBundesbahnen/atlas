package ch.sbb.atlas.servicepointdirectory.module.servicepoint.service;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointGlobalId;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.InvalidGlobalIdException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.model.GlobalId;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.repository.ServicePointGlobalIdRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GlobalIdService {

  private final ServicePointGlobalIdRepository servicePointGlobalIdRepository;

  public ReadServicePointVersionModel enrich(ReadServicePointVersionModel model) {
    servicePointGlobalIdRepository.findByServicePointNumber(model.getNumber())
        .ifPresent(mapping -> model.setGlobalId(mapping.getGlobalId()));

    return model;
  }

  public List<ReadServicePointVersionModel> enrich(List<ReadServicePointVersionModel> models) {
    Set<ServicePointNumber> numbers = models.stream()
        .map(ReadServicePointVersionModel::getNumber)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    if (numbers.isEmpty()) {
      return models;
    }

    Map<ServicePointNumber, String> globalIdByNumber = servicePointGlobalIdRepository.findByServicePointNumberIn(numbers).stream()
        .collect(Collectors.toMap(ServicePointGlobalId::getServicePointNumber, ServicePointGlobalId::getGlobalId));

    models.stream()
        .filter(model -> model.getNumber() != null)
        .forEach(model -> model.setGlobalId(globalIdByNumber.get(model.getNumber())));

    return models;
  }

  @Transactional
  public void save(ServicePointNumber servicePointNumber, GlobalId globalId) {
    validateUniqueness(servicePointNumber, globalId);
    upsert(servicePointNumber, globalId);
  }

  @Transactional
  public Optional<ServicePointNumber> saveWithRepoint(ServicePointNumber servicePointNumber, GlobalId globalId) {
    Optional<ServicePointNumber> displaced = releaseGlobalId(servicePointNumber, globalId);
    upsert(servicePointNumber, globalId);
    return displaced;
  }

  /**
   * Frees the Global-ID up if another service point holds it.
   *
   * <p>The delete is conditional on the row still holding this Global-ID, so a holder that a concurrent transaction has
   * meanwhile moved elsewhere is left alone. In that case nothing is deleted and nothing is reported; the claim that follows
   * then either succeeds because the Global-ID is genuinely free, or hits the unique index and is retried by the caller.
   */
  private Optional<ServicePointNumber> releaseGlobalId(ServicePointNumber servicePointNumber, GlobalId globalId) {
    return servicePointGlobalIdRepository.findByGlobalId(globalId.value())
        .map(ServicePointGlobalId::getServicePointNumber)
        .filter(holder -> !Objects.equals(holder, servicePointNumber))
        .filter(holder -> servicePointGlobalIdRepository.releaseGlobalIdFrom(globalId.value(), holder) > 0);
  }

  private void upsert(ServicePointNumber servicePointNumber, GlobalId globalId) {
    ServicePointGlobalId mapping = servicePointGlobalIdRepository.findByServicePointNumber(servicePointNumber)
        .map(existing -> existing.toBuilder().globalId(globalId.value()).build())
        .orElse(ServicePointGlobalId.builder()
            .servicePointNumber(servicePointNumber).globalId(globalId.value()).build());
    servicePointGlobalIdRepository.save(mapping);
  }

  @Transactional
  public void remove(ServicePointNumber servicePointNumber) {
    servicePointGlobalIdRepository.findByServicePointNumber(servicePointNumber)
        .ifPresent(servicePointGlobalIdRepository::delete);
  }

  @Transactional
  public Optional<String> removeAndGet(ServicePointNumber servicePointNumber) {
    return servicePointGlobalIdRepository.findByServicePointNumber(servicePointNumber)
        .map(mapping -> {
          servicePointGlobalIdRepository.delete(mapping);
          return mapping.getGlobalId();
        });
  }

  public void validateUniqueness(ServicePointNumber servicePointNumber, GlobalId globalId) {
    servicePointGlobalIdRepository.findByGlobalId(globalId.value())
        .filter(mapping -> !Objects.equals(mapping.getServicePointNumber(), servicePointNumber))
        .ifPresent(mapping -> {
          throw InvalidGlobalIdException.alreadyUsed(globalId);
        });
  }

}
