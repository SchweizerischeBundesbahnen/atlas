package ch.sbb.atlas.location;

import ch.sbb.atlas.api.client.location.LocationClientV1;
import ch.sbb.atlas.api.location.ClaimSloidRequestModel;
import ch.sbb.atlas.api.location.GenerateSloidRequestModel;
import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.exception.SloidAlreadyExistsException;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import feign.FeignException;
import feign.FeignException.Conflict;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LocationService {

  public static final String SLOID_PREFIX = "ch:1:sloid:";
  private final LocationClientV1 locationClient;

  private void claimSloid(ClaimSloidRequestModel request) {
    try {
      locationClient.claimSloid(request);
    } catch (Conflict e) {
      throw new SloidAlreadyExistsException(request.sloid());
    }
  }

  public void claimSloid(SloidType sloidType, String sloid) {
    claimSloid(new ClaimSloidRequestModel(sloidType, sloid));
  }

  public void claimServicePointSloid(String sloid) {
    if (sloid != null) {
      claimSloid(new ClaimSloidRequestModel(SloidType.SERVICE_POINT, sloid));
    }
  }

  public String generateTrafficPointSloid(TrafficPointElementType trafficPointElementType, ServicePointNumber servicePointNumber)
      throws FeignException {
    final SloidType sloidType = getSloidType(trafficPointElementType);
    final String sloidPrefix = SLOID_PREFIX + (servicePointNumber.getCountry() == Country.SWITZERLAND ?
        servicePointNumber.getNumberShort()
        : servicePointNumber.getNumber());
    return locationClient.generateSloid(new GenerateSloidRequestModel(sloidType, sloidPrefix));
  }

  public static SloidType getSloidType(TrafficPointElementType trafficPointElementType) {
    return trafficPointElementType == TrafficPointElementType.BOARDING_AREA ? SloidType.AREA : SloidType.PLATFORM;
  }

  public String generateSloid(SloidType sloidType, Country country) {
    return locationClient.generateSloid(new GenerateSloidRequestModel(sloidType, country));
  }

  public String generateSloid(SloidType sloidType, String sloidPrefix) {
    return locationClient.generateSloid(new GenerateSloidRequestModel(sloidType, sloidPrefix));
  }

}
