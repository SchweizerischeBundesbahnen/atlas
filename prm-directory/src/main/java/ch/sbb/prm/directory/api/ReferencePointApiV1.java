package ch.sbb.prm.directory.api;

import ch.sbb.prm.directory.controller.model.create.CreateReferencePointVersionModel;
import ch.sbb.prm.directory.controller.model.read.ReadReferencePointVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Reference Point")
@RequestMapping("v1/reference-points")
public interface ReferencePointApiV1 {

  @GetMapping
  List<ReadReferencePointVersionModel> getReferencePoints();

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadReferencePointVersionModel createReferencePoint(@RequestBody CreateReferencePointVersionModel model);

}
