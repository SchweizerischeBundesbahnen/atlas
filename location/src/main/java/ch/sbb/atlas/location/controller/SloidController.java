package ch.sbb.atlas.location.controller;

import ch.sbb.atlas.api.location.GenerateSloidRequestModel;
import ch.sbb.atlas.api.location.SloidApiV1;
import ch.sbb.atlas.location.entity.AllocatedNumberEntity;
import ch.sbb.atlas.location.repository.SloidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SloidController implements SloidApiV1 {

  private final SloidRepository sloidRepository;

  @Override
  public ResponseEntity<String> generateSloid(GenerateSloidRequestModel generateSloidRequestModel) {
    int nextSPSeqValue = sloidRepository.getNextSPSeqValue();

    while (sloidRepository.existsByNumber(nextSPSeqValue)) {
      nextSPSeqValue = sloidRepository.getNextSPSeqValue();
    }

    sloidRepository.save(new AllocatedNumberEntity(nextSPSeqValue));
    return ResponseEntity.ok("ch:1:sloid:" + nextSPSeqValue);
  }

}
