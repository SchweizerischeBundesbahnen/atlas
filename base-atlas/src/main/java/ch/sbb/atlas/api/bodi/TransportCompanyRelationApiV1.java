package ch.sbb.atlas.api.bodi;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import static ch.sbb.atlas.model.ResponseCodeDescription.NO_ENTITIES_WERE_MODIFIED;
import static ch.sbb.atlas.model.ResponseCodeDescription.VERSIONING_NOT_IMPLEMENTED;

@Tag(name = "Transport Company Relations")
@RequestMapping("v1/transport-company-relations")
public interface TransportCompanyRelationApiV1 {

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).BODI)")
  TransportCompanyBoRelationModel createTransportCompanyRelation(@RequestBody @Valid TransportCompanyRelationModel model);

  @GetMapping("{transportCompanyId}")
  List<TransportCompanyBoRelationModel> getTransportCompanyRelations(@PathVariable Long transportCompanyId);

  @DeleteMapping("{relationId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteTransportCompanyRelation(@PathVariable Long relationId);

  @PutMapping
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
          @ApiResponse(responseCode = "501", description = VERSIONING_NOT_IMPLEMENTED, content =
          @Content(schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "520", description = NO_ENTITIES_WERE_MODIFIED, content =
          @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).BODI)")
  void updateTransportCompanyRelation(@RequestBody @Valid UpdateTransportCompanyRelationModel model);
}
