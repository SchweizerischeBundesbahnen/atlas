package ch.sbb.atlas.api.location;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("v1/sloid")
public interface SloidApiV1 {

  @PostMapping("generate")
  ResponseEntity<String> generateSloid(@RequestBody GenerateSloidRequestModel generateSloidRequestModel);

}
