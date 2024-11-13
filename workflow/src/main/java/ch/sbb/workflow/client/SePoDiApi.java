package ch.sbb.workflow.client;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateDesignationOfficialServicePointModel;
import ch.sbb.atlas.model.Status;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface SePoDiApi {

    String BASEPATH = "/service-point-directory/v1/service-points/";

    @PutMapping(value = BASEPATH + "status/{sloid}/{id}")
    ReadServicePointVersionModel postServicePointsStatusUpdate(@PathVariable("sloid") String sloid, @PathVariable("id") Long id,
                                                               @RequestBody Status status);

    @PutMapping(value = BASEPATH + "/update-designation-official/{id}")
    ReadServicePointVersionModel updateServicePointDesignationOfficial(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateDesignationOfficialServicePointModel updateDesignationOfficialServicePointModel
    );

    @GetMapping(value = BASEPATH + "/versions/{id}")
    ReadServicePointVersionModel getServicePointById(
            @PathVariable("id") Long id
    );
}
