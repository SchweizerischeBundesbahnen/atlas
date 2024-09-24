package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.location.SloidHelper;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberAlreadyExistsException;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateServicePointMapper {

  private final LocationService locationService;
  private final ServicePointService servicePointService;

  public ServicePointVersion toEntity(CreateServicePointVersionModel createServicePointVersionModel) {
    if (createServicePointVersionModel.shouldGenerateServicePointNumber()) {
      // case 85,11-14
      return generateSloidAndMap(createServicePointVersionModel);
    } else {
      // case foreign country
      return checkNumberUniqueAndMap(createServicePointVersionModel);
    }
  }

  private ServicePointVersion generateSloidAndMap(CreateServicePointVersionModel createServicePointVersionModel) {
    String generatedSloid = locationService.generateSloid(SloidType.SERVICE_POINT, createServicePointVersionModel.getCountry());
    log.info("Generated new SLOID={}", generatedSloid);
    ServicePointNumber servicePointNumber = SloidHelper.getServicePointNumber(generatedSloid);
    log.info("Generated new service point number={}", servicePointNumber);
    return ServicePointVersionMapper.toEntity(createServicePointVersionModel, servicePointNumber);
  }

  private ServicePointVersion checkNumberUniqueAndMap(CreateServicePointVersionModel createServicePointVersionModel) {
    ServicePointNumber manualServicePointNumber = ServicePointNumber.of(createServicePointVersionModel.getCountry(),
        createServicePointVersionModel.getNumberShort());
    if (servicePointService.isServicePointNumberExisting(manualServicePointNumber)) {
      throw new ServicePointNumberAlreadyExistsException(manualServicePointNumber);
    }
    return ServicePointVersionMapper.toEntity(createServicePointVersionModel, manualServicePointNumber);
  }
}
