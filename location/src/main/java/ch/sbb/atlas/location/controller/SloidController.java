package ch.sbb.atlas.location.controller;

import ch.sbb.atlas.api.location.ClaimSloidRequestModel;
import ch.sbb.atlas.api.location.GenerateSloidRequestModel;
import ch.sbb.atlas.api.location.SloidApiV1;
import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.exception.SloidAlreadyExistsException;
import ch.sbb.atlas.location.service.SloidService;
import ch.sbb.atlas.location.service.SloidSyncService;
import ch.sbb.atlas.servicepoint.SloidValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SloidController implements SloidApiV1 {

  private final SloidService sloidService;
  private final SloidSyncService sloidSyncService;

  @Override
  public String generateSloid(GenerateSloidRequestModel request) {
    String sloid;
    if (request.getSloidType() == SloidType.SERVICE_POINT) {
      sloid = sloidService.getNextAvailableServicePointSloid(request.getCountry());
    } else {
      final String sloidPrefix = SloidType.transformSloidPrefix(request.getSloidType(), request.getSloidPrefix());
      sloid = sloidService.generateNewSloid(sloidPrefix, request.getSloidType());
    }
    return sloid;
  }

  @Override
  public String claimSloid(ClaimSloidRequestModel request) {
    isValidSloid(request);
    boolean claimed;
    if (request.sloidType() == SloidType.SERVICE_POINT) {
      claimed = sloidService.claimAvailableServicePointSloid(request.sloid());
    } else {
      claimed = sloidService.claimSloid(request.sloid(), request.sloidType());
    }
    if (!claimed) {
      throw new SloidAlreadyExistsException(request.sloid());
    }
    return request.sloid();
  }

  @Override
  public void sync() {
    sloidSyncService.sync();
  }

  private void isValidSloid(ClaimSloidRequestModel requestModel) {
    switch (requestModel.sloidType()) {
      case SERVICE_POINT -> SloidValidation.isSloidValid(requestModel.sloid(), SloidValidation.EXPECTED_COLONS_SERVICE_POINT);
      case AREA, TOILET, REFERENCE_POINT, PARKING_LOT, CONTACT_POINT ->
          SloidValidation.isSloidValid(requestModel.sloid(), SloidValidation.EXPECTED_COLONS_AREA);
      case PLATFORM -> SloidValidation.isSloidValid(requestModel.sloid(), SloidValidation.EXPECTED_COLONS_PLATFORM);
    }
  }
}
