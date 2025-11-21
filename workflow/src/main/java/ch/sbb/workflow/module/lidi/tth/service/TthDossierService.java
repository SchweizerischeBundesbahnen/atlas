package ch.sbb.workflow.module.lidi.tth.service;

import ch.sbb.workflow.module.lidi.tth.entity.TthDossier;
import ch.sbb.workflow.module.lidi.tth.repository.TthDossierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TthDossierService {

  private final TthDossierRepository dossierRepository;

  public TthDossier createDossier(TthDossier dossier) {
    return dossierRepository.save(dossier);
  }

}
