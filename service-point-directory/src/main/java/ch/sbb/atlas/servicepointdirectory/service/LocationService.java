package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.api.client.location.LocationClient;
import ch.sbb.atlas.api.location.ClaimSloidRequestModel;
import ch.sbb.atlas.api.location.GenerateSloidRequestModel;
import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.exception.SloidAlreadyExistsException;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {

  private final LocationClient locationClient;

  public void claimSloid(SloidType sloidType,String sloid) throws FeignException {
    try {
    locationClient.claimSloid(new ClaimSloidRequestModel(sloidType,sloid));
    } catch (FeignException e) {
      throw new SloidAlreadyExistsException(sloid);
    }
  }

  public String generateTrafficPointSloid(TrafficPointElementType trafficPointElementType, ServicePointNumber servicePointNumber)
      throws FeignException {
    final SloidType sloidType =
        trafficPointElementType == TrafficPointElementType.BOARDING_AREA ? SloidType.AREA : SloidType.PLATFORM;
    final String sloidPrefix = "ch:1:sloid:" + (servicePointNumber.getCountry() == Country.SWITZERLAND ?
        servicePointNumber.getNumberShort()
        : servicePointNumber.getNumber());
    return locationClient.generateSloid(new GenerateSloidRequestModel(sloidType, sloidPrefix));
  }

  public String generateSloid(SloidType sloidType, Country country) {
    return locationClient.generateSloid(new GenerateSloidRequestModel(sloidType,country));
  }
}
