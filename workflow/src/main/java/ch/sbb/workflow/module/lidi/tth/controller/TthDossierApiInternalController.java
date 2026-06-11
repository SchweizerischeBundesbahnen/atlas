package ch.sbb.workflow.module.lidi.tth.controller;

import static ch.sbb.atlas.model.Language.toLocale;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.workflow.tth.dossier.BoAnswerModel;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierModel;
import ch.sbb.atlas.model.Language;
import ch.sbb.workflow.module.lidi.tth.api.TthDossierApiInternal;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossier;
import ch.sbb.workflow.module.lidi.tth.mapper.TthDossierMapper;
import ch.sbb.workflow.module.lidi.tth.model.TthDossierTuCsvModel;
import ch.sbb.workflow.module.lidi.tth.search.TthDossierRequestParams;
import ch.sbb.workflow.module.lidi.tth.search.TthDossierSearchRestrictions;
import ch.sbb.workflow.module.lidi.tth.service.TthDossierCsvExportService;
import ch.sbb.workflow.module.lidi.tth.service.TthDossierService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TthDossierApiInternalController implements TthDossierApiInternal {

  private final TthDossierService tthDossierService;
  private final TthDossierCsvExportService tthDossierCsvExportService;

  @Override
  public Container<TthDossierModel> getDossiers(Pageable pageable, TthDossierRequestParams requestParams) {
    Page<TthDossier> dossiers = tthDossierService.getDossiers(TthDossierSearchRestrictions.builder()
        .pageable(pageable)
        .requestParams(requestParams)
        .build());
    return Container.<TthDossierModel>builder()
        .objects(dossiers.stream().map(TthDossierMapper::toModel).toList())
        .totalCount(dossiers.getTotalElements())
        .build();
  }

  @Override
  public Resource getDossiersCsv(TthDossierRequestParams requestParams, Language lang) {
    Page<TthDossier> dossiers = tthDossierService.getDossiers(
        TthDossierSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .requestParams(requestParams)
            .build()
    );
    List<TthDossierTuCsvModel> csvRows = tthDossierCsvExportService.getTthDossierTuCsvModels(dossiers);
    File csvFile = tthDossierCsvExportService.writeCsv(csvRows, TthDossierTuCsvModel.class, toLocale(lang));
    try {
      return new InputStreamResource(new FileInputStream(csvFile));
    } catch (IOException e) {
      throw new FileException(e);
    }
  }

  @Override
  public TthDossierModel getDossier(Long dossierId) {
    return TthDossierMapper.toModel(tthDossierService.getDossierById(dossierId));
  }

  @Override
  public TthDossierModel createDossier(TthDossierModel dossierModel) {
    return TthDossierMapper.toModel(tthDossierService.createDossier(TthDossierMapper.toEntity(dossierModel)));
  }

  @Override
  public void sendDossierToBo(Long dossierId) {
    tthDossierService.sendDossierToBo(tthDossierService.getDossierById(dossierId));
  }

  @Override
  public void answerQuestion(Long questionId, BoAnswerModel boAnswer) {
    TthDossier tthDossier = tthDossierService.getDossierByQuestionId(questionId);
    tthDossierService.answerQuestion(questionId, boAnswer.getAnswerToCanton(), tthDossier);
  }

  @Override
  public void completeDossier(Long dossierId, DossierStatus status) {
    TthDossier tthDossier = tthDossierService.getDossierById(dossierId);
    tthDossierService.completeDossier(tthDossier, status);
  }

  @Override
  public TthDossierModel updateDossier(Long dossierId, TthDossierModel dossierModel) {
    dossierModel.setId(dossierId);
    return TthDossierMapper.toModel(tthDossierService.updateDossier(dossierId, TthDossierMapper.toEntity(dossierModel)));
  }
}
