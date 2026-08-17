package ch.sbb.timetable.hearing.api;

import ch.sbb.atlas.annotation.AuthorizedOnly;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.workflow.tth.dossier.BoAnswerModel;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierModel;
import ch.sbb.atlas.model.Language;
import ch.sbb.timetable.hearing.entity.Dossier;
import ch.sbb.timetable.hearing.search.DossierRequestParams;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "TTH Dossier")
@RequestMapping(DossierApiInternal.BASE_PATH)
public interface DossierApiInternal {

  String BASE_PATH = "/internal/tth/dossier";

  @AuthorizedOnly
  @GetMapping
  @PageableAsQueryParam
  Container<TthDossierModel> getDossiers(@Parameter(hidden = true) @PageableDefault(sort = {Dossier.Fields.id,
      Dossier.Fields.topic}) Pageable pageable, @ParameterObject DossierRequestParams requestParams);

  @PreAuthorize("""
      @cantonBasedUserAdministrationService.isAtLeastExplicitReader(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).TIMETABLE_HEARING)
      || @boUserMailCheckService.isCurrentUserSbbUidAssignedTo(#requestParams.getBoContactSbbuid())""")
  @GetMapping(path = "csv", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  Resource getDossiersCsv(@ParameterObject DossierRequestParams requestParams, @RequestParam Language lang);

  @AuthorizedOnly
  @GetMapping("{dossierId}")
  TthDossierModel getDossier(@PathVariable Long dossierId);

  @AuthorizedOnly
  @PostMapping
  TthDossierModel createDossier(@Valid @RequestBody TthDossierModel dossierModel);

  @AuthorizedOnly
  @PostMapping("{dossierId}/send-to-bo")
  void sendDossierToBo(@PathVariable Long dossierId);

  @AuthorizedOnly
  @PostMapping("/answer/{questionId}")
  void answerQuestion(@PathVariable Long questionId, @Valid @RequestBody BoAnswerModel boAnswer);

  @AuthorizedOnly
  @PostMapping("{dossierId}/complete/{status}")
  void completeDossier(@PathVariable Long dossierId, @PathVariable DossierStatus status);

  @AuthorizedOnly
  @PutMapping("{dossierId}")
  TthDossierModel updateDossier(@PathVariable Long dossierId, @Valid @RequestBody TthDossierModel dossierModel);
}
