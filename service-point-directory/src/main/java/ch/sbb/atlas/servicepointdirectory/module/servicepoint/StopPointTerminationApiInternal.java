package ch.sbb.atlas.servicepointdirectory.module.servicepoint;

import ch.sbb.atlas.annotation.AdminOnly;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.StopPointWorkflowTerminationModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Stop Point Termination")
@RequestMapping("internal/service-points/termination")
@Validated
public interface StopPointTerminationApiInternal {

  @PostMapping(path = "/start/{sloid}/{id}")
  ReadServicePointVersionModel startServicePointTermination(@PathVariable String sloid, @PathVariable Long id,
      @RequestBody @Valid UpdateTerminationServicePointModel updateTerminationServicePointModel);

  @AdminOnly
  @PostMapping(path = "/stop/{sloid}/{id}")
  ReadServicePointVersionModel stopServicePointTermination(@PathVariable String sloid, @PathVariable Long id);

  @AdminOnly
  @PostMapping(value = "/terminate")
  void terminateStopPointByWorkflow(@RequestBody @Valid StopPointWorkflowTerminationModel terminationModel);

  @AdminOnly
  @PostMapping(value = "/change-to-tariff-stop")
  void changeToTariffStop(@RequestBody @Valid StopPointWorkflowTerminationModel terminationModel);
}
