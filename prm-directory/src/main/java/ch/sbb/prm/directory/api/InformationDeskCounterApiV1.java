package ch.sbb.prm.directory.api;

import ch.sbb.prm.directory.controller.model.informationdesk.CreateInformationDeskVersionModel;
import ch.sbb.prm.directory.controller.model.informationdesk.ReadInformationDeskVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "PRM - Person with Reduced Mobility")
@RequestMapping("v1/information-desks")
public interface InformationDeskCounterApiV1 {

  @GetMapping
  List<ReadInformationDeskVersionModel> getInformationDesks();

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadInformationDeskVersionModel createStopPlace(@RequestBody @Valid CreateInformationDeskVersionModel model);
}
