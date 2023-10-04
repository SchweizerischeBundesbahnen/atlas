package ch.sbb.prm.directory.api;

import ch.sbb.prm.directory.controller.model.StopPlaceVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Stop Places")
@RequestMapping("v1/stop-places")
public interface StopPlaceApiV1 {

  @GetMapping
  List<StopPlaceVersionModel> getAllStopPaces();

}
