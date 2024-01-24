package ch.sbb.atlas.location.controller;

import ch.sbb.atlas.api.location.ClaimSloidRequestModel;
import ch.sbb.atlas.api.location.GenerateSloidRequestModel;
import ch.sbb.atlas.api.location.SloidApiV1;
import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.location.service.SloidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SloidController implements SloidApiV1 {

  private final SloidService sloidService;

  @Override
  public ResponseEntity<String> generateSloid(GenerateSloidRequestModel request) {
    String sloid;
    if (request.getSloidType() == SloidType.SERVICE_POINT) {
      sloid = sloidService.getNextAvailableSloid(request.getCountry());
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
      claimed = sloidService.claimAvailableSloid(request.sloid());
    } else {
      claimed = sloidService.claimSloid(request.sloid(), request.sloidType());
    }
    return claimed ? ResponseEntity.ok(request.sloid())
        : ResponseEntity.status(HttpStatus.CONFLICT).body(request.sloid() + " is not available");
  }

  @Override
  public ResponseEntity<Void> sync() {
    sloidService.sync();
    return ResponseEntity.noContent().build();
  }

}
