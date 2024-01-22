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
      final String sloidPrefix = request.getSloidType().getSloidPrefix(request.getSloidPrefix());
      final String seqName = request.getSloidType().getSeqName();
      sloid = sloidService.generateNewSloid(sloidPrefix, seqName);
    }
    return ResponseEntity.ok(sloid);
  }

  @Override
  public ResponseEntity<String> claimSloid(ClaimSloidRequestModel request) {
    boolean claimed;
    if (request.getSloidType() == SloidType.SERVICE_POINT) {
      claimed = sloidService.claimAvailableSloid(request.getSloid(), request.getCountry());
    } else {
      claimed = sloidService.claimSloid(request.getSloid());
    }
    return claimed ? ResponseEntity.ok(request.getSloid())
        : ResponseEntity.status(HttpStatus.CONFLICT).body(request.getSloid() + " is not available");
  }

  @Override
  public ResponseEntity<String> sync() {
    return ResponseEntity.ok(sloidService.sync());
  }

}
