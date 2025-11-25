package ch.sbb.workflow.module.lidi.tth.api;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierModel;
import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierQuestionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "TTH Dossier")
@RequestMapping(TthDossierApiInternal.BASE_PATH)
public interface TthDossierApiInternal {

  String BASE_PATH = "/internal/tth/dossier";

  @GetMapping("{dossierId}")
  TthDossierModel getDossier(@PathVariable Long dossierId);

  @PostMapping
  TthDossierModel createDossier(@Valid @RequestBody TthDossierModel dossierModel);

  @PostMapping("{dossierId}/send-to-tu")
  void sendDossierToBo(@PathVariable Long dossierId, @Valid @RequestBody TthDossierQuestionModel questionModel);

}
