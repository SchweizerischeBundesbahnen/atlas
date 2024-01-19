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
    if (request.sloidType() == SloidType.SERVICE_POINT) {
      sloid = sloidService.getNextAvailableSloid(request.country());
    } else {
      final String sloidPrefix = request.sloidType().getSloidPrefix(request.sloidPrefix());
      final String seqName = request.sloidType().getSeqName();
      sloid = sloidService.generateNewSloid(sloidPrefix, seqName);
    }
    return ResponseEntity.ok(sloid);
  }

  @Override
  public ResponseEntity<String> claimSloid(ClaimSloidRequestModel request) {
    boolean claimed;
    if (request.sloidType() == SloidType.SERVICE_POINT) {
      claimed = sloidService.claimAvailableSloid(request.sloid(), request.country());
    } else {
      claimed = sloidService.claimSloid(request.sloid());
    }
    return claimed ? ResponseEntity.ok(request.sloid())
        : ResponseEntity.status(HttpStatus.CONFLICT).body(request.sloid() + " is not available");
  }

}
