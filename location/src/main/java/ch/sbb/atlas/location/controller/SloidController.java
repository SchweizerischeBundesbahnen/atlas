package ch.sbb.atlas.location.controller;

import ch.sbb.atlas.api.location.ClaimSloidRequestModel;
import ch.sbb.atlas.api.location.GenerateSloidRequestModel;
import ch.sbb.atlas.api.location.SloidApiV1;
import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.location.service.SloidService;
import ch.sbb.atlas.location.service.SloidSynchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SloidController implements SloidApiV1 {

  private final SloidService sloidService;
  private final SloidSynchService sloidSynchService;

  @Override
  public ResponseEntity<String> generateSloid(GenerateSloidRequestModel request) {
    String sloid;
    if (request.getSloidType() == SloidType.SERVICE_POINT) {
      sloid = sloidService.getNextAvailableServicePointSloid(request.getCountry());
    } else {
      final String sloidPrefix = SloidType.transformSloidPrefix(request.getSloidType(), request.getSloidPrefix());
      sloid = sloidService.generateNewSloid(sloidPrefix, request.getSloidType());
    }
    return ResponseEntity.ok(sloid);
  }

  @Override
  public ResponseEntity<String> claimSloid(ClaimSloidRequestModel request) {
    boolean claimed;
    if (request.sloidType() == SloidType.SERVICE_POINT) {
      claimed = sloidService.claimAvailableServicePointSloid(request.sloid());
    } else {
      claimed = sloidService.claimSloid(request.sloid(), request.sloidType());
    }
    return claimed ? ResponseEntity.ok(request.sloid())
        : ResponseEntity.status(HttpStatus.CONFLICT).body(request.sloid() + " is not available");
  }

  @Override
  public ResponseEntity<Void> sync() {
    sloidSynchService.sync();
    return ResponseEntity.noContent().build();
  }

}
