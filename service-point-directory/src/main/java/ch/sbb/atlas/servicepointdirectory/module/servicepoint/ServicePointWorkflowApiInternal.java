package ch.sbb.atlas.servicepointdirectory.module.servicepoint;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateDesignationOfficialServicePointModel;
import ch.sbb.atlas.model.Status;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Service Points")
@RequestMapping("internal/service-points")
@Validated
public interface ServicePointWorkflowApiInternal {

  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).SEPODI)")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping({"versions/{id}/skip-workflow"})
  ReadServicePointVersionModel validateServicePoint(@PathVariable Long id);

  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).SEPODI)")
  @PutMapping(path = "/update-designation-official/{id}")
  ReadServicePointVersionModel updateDesignationOfficial(
      @PathVariable Long id,
      @RequestBody @Valid UpdateDesignationOfficialServicePointModel updateDesignationOfficialServicePointModel
  );

  @PutMapping(path = "/status/{sloid}/{id}")
  ReadServicePointVersionModel updateServicePointStatus(@PathVariable String sloid, @PathVariable Long id,
      @RequestBody @Valid Status status);

}
