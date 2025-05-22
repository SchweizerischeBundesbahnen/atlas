package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Stop Point Termination")
@RequestMapping("v1/service-points/termination")
@Validated
public interface StopPointTerminationApiV1 {

  @PutMapping(path = "/start/{sloid}/{id}")
  ReadServicePointVersionModel startServicePointTermination(@PathVariable String sloid, @PathVariable Long id,
      @RequestBody @Valid UpdateTerminationServicePointModel updateTerminationServicePointModel);

  @PutMapping(path = "/stop/{sloid}/{id}")
  ReadServicePointVersionModel stopServicePointTermination(@PathVariable String sloid, @PathVariable Long id);

}
