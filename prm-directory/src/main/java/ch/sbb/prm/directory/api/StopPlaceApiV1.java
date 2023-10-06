package ch.sbb.prm.directory.api;

import ch.sbb.prm.directory.controller.model.stopplace.CreateStopPlaceVersionModel;
import ch.sbb.prm.directory.controller.model.stopplace.ReadStopPlaceVersionModel;
import ch.sbb.prm.directory.controller.model.stopplace.StopPlaceVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Stop Places")
@RequestMapping("v1/stop-places")
public interface StopPlaceApiV1 {

  @GetMapping
  List<ReadStopPlaceVersionModel> getAllStopPaces();

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  StopPlaceVersionModel createStopPlace(@RequestBody CreateStopPlaceVersionModel stopPlaceVersionModel);

}
