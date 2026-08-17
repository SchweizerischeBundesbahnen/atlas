package ch.sbb.timetable.hearing.controller;

import static ch.sbb.atlas.model.Language.toLocale;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.workflow.tth.dossier.BoAnswerModel;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierModel;
import ch.sbb.atlas.model.Language;
import ch.sbb.timetable.hearing.api.DossierApiInternal;
import ch.sbb.timetable.hearing.entity.Dossier;
import ch.sbb.timetable.hearing.mapper.DossierMapper;
import ch.sbb.timetable.hearing.model.DossierTuCsvModel;
import ch.sbb.timetable.hearing.search.DossierRequestParams;
import ch.sbb.timetable.hearing.search.DossierSearchRestrictions;
import ch.sbb.timetable.hearing.service.DossierCsvExportService;
import ch.sbb.timetable.hearing.service.DossierService;
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
public class DossierApiInternalController implements DossierApiInternal {

  private final DossierService dossierService;
  private final DossierCsvExportService dossierCsvExportService;

  @Override
  public Container<TthDossierModel> getDossiers(Pageable pageable, DossierRequestParams requestParams) {
    Page<Dossier> dossiers = dossierService.getDossiers(DossierSearchRestrictions.builder()
        .pageable(pageable)
        .requestParams(requestParams)
        .build());
    return Container.<TthDossierModel>builder()
        .objects(dossiers.stream().map(DossierMapper::toModel).toList())
        .totalCount(dossiers.getTotalElements())
        .build();
  }

  @Override
  public Resource getDossiersCsv(DossierRequestParams requestParams, Language lang) {
    Page<Dossier> dossiers = dossierService.getDossiers(
        DossierSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .requestParams(requestParams)
            .build()
    );
    List<DossierTuCsvModel> csvRows = dossierCsvExportService.getTthDossierTuCsvModels(dossiers);
    File csvFile = dossierCsvExportService.writeCsv(csvRows, DossierTuCsvModel.class, toLocale(lang));
    try {
      return new InputStreamResource(new FileInputStream(csvFile));
    } catch (IOException e) {
      throw new FileException(e);
    }
  }

  @Override
  public TthDossierModel getDossier(Long dossierId) {
    return DossierMapper.toModel(dossierService.getDossierById(dossierId));
  }

  @Override
  public TthDossierModel createDossier(TthDossierModel dossierModel) {
    return DossierMapper.toModel(dossierService.createDossier(DossierMapper.toEntity(dossierModel)));
  }

  @Override
  public void sendDossierToBo(Long dossierId) {
    dossierService.sendDossierToBo(dossierService.getDossierById(dossierId));
  }

  @Override
  public void answerQuestion(Long questionId, BoAnswerModel boAnswer) {
    Dossier dossier = dossierService.getDossierByQuestionId(questionId);
    dossierService.answerQuestion(questionId, boAnswer.getAnswerToCanton(), dossier);
  }

  @Override
  public void completeDossier(Long dossierId, DossierStatus status) {
    Dossier dossier = dossierService.getDossierById(dossierId);
    dossierService.completeDossier(dossier, status);
  }

  @Override
  public TthDossierModel updateDossier(Long dossierId, TthDossierModel dossierModel) {
    dossierModel.setId(dossierId);
    return DossierMapper.toModel(dossierService.updateDossier(dossierId, DossierMapper.toEntity(dossierModel)));
  }
}
