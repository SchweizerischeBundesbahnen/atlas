package ch.sbb.prm.directory.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Reference Point")
@RequestMapping("v1/reference-points")
public interface ReferencePointApiV1 {

  @GetMapping
  List<ReferencePointVersionModel> getReferencePoints();

}
