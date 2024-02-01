package ch.sbb.prm.directory.api;

import ch.sbb.atlas.api.prm.model.contactpoint.ContactPointVersionModel;
import ch.sbb.atlas.api.prm.model.contactpoint.ReadContactPointVersionModel;
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
@RequestMapping("v1/contact-points")
public interface ContactPointApiV1 {

  @GetMapping
  List<ReadContactPointVersionModel> getContactPoints();

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadContactPointVersionModel createContactPoint(@RequestBody @Valid ContactPointVersionModel model);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadContactPointVersionModel> updateContactPoint(@PathVariable Long id,
                                                           @RequestBody @Valid ContactPointVersionModel model);
}
