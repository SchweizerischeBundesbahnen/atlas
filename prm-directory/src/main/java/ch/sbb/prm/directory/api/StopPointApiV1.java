package ch.sbb.prm.directory.api;

import ch.sbb.atlas.api.prm.model.stoppoint.CreateStopPointVersionModel;
import ch.sbb.atlas.api.prm.model.stoppoint.ReadStopPointVersionModel;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointImportRequestModel;
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
@RequestMapping("v1/stop-points")
public interface StopPointApiV1 {

  @GetMapping
  List<ReadStopPointVersionModel> getAllStopPoints();

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadStopPointVersionModel createStopPoint(@RequestBody @Valid CreateStopPointVersionModel stopPointVersionModel);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadStopPointVersionModel> updateStopPoint(@PathVariable Long id,
      @RequestBody @Valid CreateStopPointVersionModel stopPointVersionModel);

  @PostMapping("import")
  List<ItemImportResult> importStopPoints(@RequestBody @Valid StopPointImportRequestModel importRequestModel);

}
