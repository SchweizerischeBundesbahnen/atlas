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

  public void validateUniqueness(ServicePointNumber servicePointNumber, GlobalId globalId) {
    servicePointGlobalIdRepository.findByGlobalId(globalId.value())
        .filter(mapping -> !Objects.equals(mapping.getServicePointNumber(), servicePointNumber))
        .ifPresent(mapping -> {
          throw InvalidGlobalIdException.alreadyUsed(globalId);
        });
  }

}
