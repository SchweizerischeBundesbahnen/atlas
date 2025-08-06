package ch.sbb.workflow.sepodi.client;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.StopPointWorkflowTerminationModel;
import ch.sbb.atlas.api.servicepoint.UpdateDesignationOfficialServicePointModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.model.Status;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface SePoDiApi {

  String BASEPATH = "/service-point-directory/v1/service-points/";
  String TERMINATION_BASEPATH = "/service-point-directory/internal/service-points/termination";

  @PutMapping(value = BASEPATH + "status/{sloid}/{id}")
  ReadServicePointVersionModel postServicePointsStatusUpdate(@PathVariable("sloid") String sloid, @PathVariable("id") Long id,
      @RequestBody Status status);

  @PutMapping(value = BASEPATH + "/update-designation-official/{id}")
  ReadServicePointVersionModel updateServicePointDesignationOfficial(
      @PathVariable("id") Long id,
      @RequestBody @Valid UpdateDesignationOfficialServicePointModel updateDesignationOfficialServicePointModel
  );

  @GetMapping(value = BASEPATH + "/versions/{id}")
  ReadServicePointVersionModel getServicePointById(@PathVariable("id") Long id);

  @PostMapping(value = TERMINATION_BASEPATH + "/start/{sloid}/{id}")
  ReadServicePointVersionModel startServicePointTermination(@PathVariable("sloid") String sloid,
      @PathVariable("id") Long id, @RequestBody @Valid UpdateTerminationServicePointModel updateTerminationServicePointModel);

  @PostMapping(value = TERMINATION_BASEPATH + "/stop/{sloid}/{id}")
  ReadServicePointVersionModel stopServicePointTermination(@PathVariable("sloid") String sloid,
      @PathVariable("id") Long id);

  @PostMapping(value = TERMINATION_BASEPATH + "/terminate")
  void terminateStopPoint(@RequestBody @Valid StopPointWorkflowTerminationModel terminationModel);

  @PostMapping(value = TERMINATION_BASEPATH + "/change-to-tariff-stop")
  void changeToTariffStop(@RequestBody @Valid StopPointWorkflowTerminationModel terminationModel);
}
