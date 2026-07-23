package ch.sbb.atlas.servicepointdirectory.module.servicepoint.controller;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.ServicePointGlobalIdApiInternal;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.globalid.GlobalIdUpdateModel;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.mapper.ServicePointVersionMapper;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.model.GlobalId;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.service.GlobalIdService;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.service.ServicePointService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ServicePointGlobalIdApiInternalController implements ServicePointGlobalIdApiInternal {

  private final ServicePointService servicePointService;
  private final GlobalIdService globalIdService;

  @Override
  public List<ReadServicePointVersionModel> updateGlobalId(Integer servicePointNumber, GlobalIdUpdateModel globalId) {
    ServicePointNumber number = ServicePointNumber.ofNumberWithoutCheckDigit(servicePointNumber);
    List<ServicePointVersion> versions = findExistingVersions(number);

    globalIdService.save(number, GlobalId.of(globalId.getGlobalId(), number.getCountry()));

    return toModels(versions);
  }

  @Override
  public List<ReadServicePointVersionModel> deleteGlobalId(Integer servicePointNumber) {
    ServicePointNumber number = ServicePointNumber.ofNumberWithoutCheckDigit(servicePointNumber);
    List<ServicePointVersion> versions = findExistingVersions(number);

    globalIdService.remove(number);

    return toModels(versions);
  }

  private List<ServicePointVersion> findExistingVersions(ServicePointNumber number) {
    List<ServicePointVersion> versions = servicePointService.findAllByNumberOrderByValidFrom(number);
    if (versions.isEmpty()) {
      throw new ServicePointNumberNotFoundException(number);
    }
    return versions;
  }

  private List<ReadServicePointVersionModel> toModels(List<ServicePointVersion> versions) {
    return globalIdService.enrich(versions.stream().map(ServicePointVersionMapper::toModel).toList());
  }

}
