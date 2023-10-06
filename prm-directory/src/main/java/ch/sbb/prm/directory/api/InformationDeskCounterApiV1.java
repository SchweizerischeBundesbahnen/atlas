package ch.sbb.prm.directory.api;

import ch.sbb.prm.directory.controller.model.infromationdesk.CreateInformationDeskVersionModel;
import ch.sbb.prm.directory.controller.model.infromationdesk.ReadInformationDeskVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "InformationDesk")
@RequestMapping("v1/information-desks")
public interface InformationDeskCounterApiV1 {

  @GetMapping
  List<ReadInformationDeskVersionModel> getInformationDesks();

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadInformationDeskVersionModel createStopPlace(@RequestBody CreateInformationDeskVersionModel model);
}
