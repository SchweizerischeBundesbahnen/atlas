package ch.sbb.prm.directory.api;

import ch.sbb.atlas.api.prm.model.referencepoint.ReadReferencePointVersionModel;
import ch.sbb.atlas.api.prm.model.referencepoint.ReferencePointVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Person with Reduced Mobility")
@RequestMapping("v1/reference-points")
public interface ReferencePointApiV1 {

  @GetMapping
  List<ReadReferencePointVersionModel> getReferencePoints();

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadReferencePointVersionModel createReferencePoint(@RequestBody @Valid ReferencePointVersionModel model);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadReferencePointVersionModel> updateReferencePoint(@PathVariable Long id,
      @RequestBody @Valid ReferencePointVersionModel model);

}
