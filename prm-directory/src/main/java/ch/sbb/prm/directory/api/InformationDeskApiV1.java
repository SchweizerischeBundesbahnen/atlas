package ch.sbb.prm.directory.api;

import ch.sbb.atlas.api.prm.model.informationdesk.InformationDeskVersionModel;
import ch.sbb.atlas.api.prm.model.informationdesk.ReadInformationDeskVersionModel;
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
@RequestMapping("v1/information-desks")
public interface InformationDeskApiV1 {

  @GetMapping
  List<ReadInformationDeskVersionModel> getInformationDesks();

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadInformationDeskVersionModel createInformationDesk(@RequestBody @Valid InformationDeskVersionModel model);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadInformationDeskVersionModel> updateInformationDesk(@PathVariable Long id,
      @RequestBody @Valid InformationDeskVersionModel model);
}
