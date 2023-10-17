package ch.sbb.prm.directory.api;

import ch.sbb.atlas.api.prm.model.stopplace.CreateStopPlaceVersionModel;
import ch.sbb.atlas.api.prm.model.stopplace.ReadStopPlaceVersionModel;
import ch.sbb.atlas.imports.prm.stopplace.StopPlaceImportRequestModel;
import ch.sbb.atlas.imports.servicepoint.ItemImportResult;
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

@Tag(name = "PRM - Person with Reduced Mobility")
@RequestMapping("v1/stop-places")
public interface StopPlaceApiV1 {

  @GetMapping
  List<ReadStopPlaceVersionModel> getAllStopPaces();

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadStopPlaceVersionModel createStopPlace(@RequestBody @Valid CreateStopPlaceVersionModel stopPlaceVersionModel);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadStopPlaceVersionModel> updateStopPlace(@PathVariable Long id,
      @RequestBody @Valid CreateStopPlaceVersionModel stopPlaceVersionModel);

  @PostMapping("import")
  List<ItemImportResult> importServicePoints(@RequestBody @Valid StopPlaceImportRequestModel importRequestModel);

}
