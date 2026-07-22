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
import org.apache.commons.lang3.StringUtils;
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
    List<ServicePointVersion> versions = servicePointService.findAllByNumberOrderByValidFrom(number);
    if (versions.isEmpty()) {
      throw new ServicePointNumberNotFoundException(number);
    }

    String rawGlobalId = globalId.getGlobalId();
    if (StringUtils.isBlank(rawGlobalId)) {
      globalIdService.remove(number);
    } else {
      globalIdService.save(number, GlobalId.of(rawGlobalId, number.getCountry()));
    }

    return globalIdService.enrich(versions.stream().map(ServicePointVersionMapper::toModel).toList());
  }

}
