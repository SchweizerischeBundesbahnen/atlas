package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoLocationModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointApiInternal;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointSwissWithGeoMapper;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointVersionMapper;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointSwissWithGeoTransfer;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ServicePointApiInternalController implements ServicePointApiInternal {

  private final ServicePointService servicePointService;

  @Override
  public List<ReadServicePointVersionModel> revokeServicePoint(Integer servicePointNumber) {
    List<ReadServicePointVersionModel> servicePointVersionModels = servicePointService.revokeServicePoint(
            ServicePointNumber.ofNumberWithoutCheckDigit(servicePointNumber))
        .stream()
        .map(ServicePointVersionMapper::toModel)
        .toList();
    if (servicePointVersionModels.isEmpty()) {
      throw new ServicePointNumberNotFoundException(ServicePointNumber.ofNumberWithoutCheckDigit(servicePointNumber));
    }
    return servicePointVersionModels;
  }

  @Override
  public void syncServicePoints() {
    servicePointService.publishAllServicePoints();
  }

  @Override
  public List<ServicePointSwissWithGeoLocationModel> getActualServicePointWithGeolocation() {
    List<ServicePointSwissWithGeoTransfer> actualServicePointWithGeolocation =
        servicePointService.findActualServicePointWithGeolocation();

    List<ServicePointSwissWithGeoLocationModel> swissWithGeoModels = new ArrayList<>();
    actualServicePointWithGeolocation.stream()
        .collect(Collectors.groupingBy(ServicePointSwissWithGeoTransfer::getSloid))
        .forEach((sloid, swissWithGeoTransfers) ->
            swissWithGeoModels.add(ServicePointSwissWithGeoMapper.toModel(sloid, swissWithGeoTransfers)));

    return swissWithGeoModels;
  }

}
