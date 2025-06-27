package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Client Credential Administration")
@RequestMapping("/v1/client-credentials")
public interface ClientCredentialAdministrationApiV1 {

  @GetMapping
  @PageableAsQueryParam
  @Operation(description = "Retrieve Overview for all the managed Users")
  Container<ClientCredentialModel> getClientCredentials(@Parameter(hidden = true) Pageable pageable);

  @GetMapping("{clientId}")
  @Operation(description = "Retrieve Information for a given clientId")
  ClientCredentialModel getClientCredential(@PathVariable String clientId);

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(description = "Register a client")
  ClientCredentialModel createClientCredential(@RequestBody @Valid ClientCredentialCreateModel client);

  @PutMapping("/{clientId}/{application}")
  @Operation(description = "Update the permissions of a client")
  ClientCredentialModel updateClientCredential(@PathVariable String clientId,
      @PathVariable ApplicationType application,
      @RequestBody @Valid PermissionModel editedPermissions);

}
