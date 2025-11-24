package ch.sbb.workflow.module.lidi.tth.api;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "TTH Dossier")
@RequestMapping(TthDossierApiInternal.BASE_PATH)
public interface TthDossierApiInternal {

  String BASE_PATH = "/internal/tth/dossier";

  @PostMapping
  TthDossierModel createDossier(@Valid @RequestBody TthDossierModel dossierModel);

}
