package ch.sbb.workflow.module.lidi.tth.controller;

import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierModel;
import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierQuestionModel;
import ch.sbb.workflow.module.lidi.tth.api.TthDossierApiInternal;
import ch.sbb.workflow.module.lidi.tth.mapper.TthDossierMapper;
import ch.sbb.workflow.module.lidi.tth.mapper.TthDossierQuestionMapper;
import ch.sbb.workflow.module.lidi.tth.service.TthDossierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TthDossierController implements TthDossierApiInternal {

  private final TthDossierService tthDossierService;

  @Override
  public TthDossierModel getDossier(Long dossierId) {
    return TthDossierMapper.toModel(tthDossierService.getDossierById(dossierId));
  }

  @Override
  public TthDossierModel createDossier(TthDossierModel dossierModel) {
    return TthDossierMapper.toModel(tthDossierService.createDossier(TthDossierMapper.toEntity(dossierModel)));
  }

  @Override
  public void sendDossierToBo(Long dossierId, TthDossierQuestionModel questionModel) {
    tthDossierService.sendDossierToBo(dossierId, TthDossierQuestionMapper.toEntity(questionModel));
  }
}
