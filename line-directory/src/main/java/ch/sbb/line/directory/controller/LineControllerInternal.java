package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.lidi.AffectedSublinesModel;
import ch.sbb.atlas.api.lidi.LineApiInternal;
import ch.sbb.atlas.api.lidi.LineVersionSnapshotModel;
import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV2;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersionSnapshot;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.mapper.LineMapper;
import ch.sbb.line.directory.mapper.LineVersionSnapshotMapper;
import ch.sbb.line.directory.model.search.LineVersionSnapshotSearchRestrictions;
import ch.sbb.line.directory.service.LineService;
import ch.sbb.line.directory.service.LineVersionSnapshotService;
import ch.sbb.line.directory.service.SublineShorteningService;
import ch.sbb.line.directory.service.export.LineVersionExportService;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LineControllerInternal implements LineApiInternal {

  private final LineService lineService;
  private final LineVersionExportService lineVersionExportService;
  private final LineVersionSnapshotService lineVersionSnapshotService;
  private final SublineShorteningService sublineShorteningService;

  @Override
  public void revokeLine(String slnid) {
    List<LineVersion> lineVersions = lineService.revokeLine(slnid);
    if (lineVersions.isEmpty()) {
      throw new SlnidNotFoundException(slnid);
    }
  }

  @Override
  public void deleteLines(String slnid) {
    lineService.deleteAll(slnid);
  }

  @Override
  public void skipWorkflow(Long id) {
    lineService.skipWorkflow(id);
  }

  @Deprecated(forRemoval = true)
  @Override
  public List<URL> exportFullLineVersions() {
    return lineVersionExportService.exportFullVersions();
  }

  @Deprecated(forRemoval = true)
  @Override
  public List<URL> exportActualLineVersions() {
    return lineVersionExportService.exportActualVersions();
  }

  @Deprecated(forRemoval = true)
  @Override
  public List<URL> exportFutureTimetableLineVersions() {
    return lineVersionExportService.exportFutureTimetableVersions();
  }

  @Override
  public Container<LineVersionSnapshotModel> getLineVersionSnapshot(Pageable pageable, List<String> searchCriteria,
      Optional<LocalDate> validOn, List<WorkflowStatus> statusChoices) {
    log.info(
        "Load LineVersionSnapshot using pageable={}, searchCriteriaSpecification={}, validOn={}", pageable, searchCriteria,
        validOn);
    Page<LineVersionSnapshot> lineVersionSnapshotPage = lineVersionSnapshotService.findAll(
        LineVersionSnapshotSearchRestrictions.builder()
            .pageable(pageable)
            .searchCriterias(searchCriteria)
            .statusRestrictions(statusChoices)
            .validOn(validOn)
            .build());
    List<LineVersionSnapshotModel> lineVersionSnapshotModels = lineVersionSnapshotPage.stream()
        .map(LineVersionSnapshotMapper::toModel).toList();
    return Container.<LineVersionSnapshotModel>builder()
        .objects(lineVersionSnapshotModels)
        .totalCount(lineVersionSnapshotPage.getTotalElements())
        .build();
  }

  @Override
  public LineVersionSnapshotModel getLineVersionSnapshotById(Long id) {
    return LineVersionSnapshotMapper.toModel(lineVersionSnapshotService.getLineVersionSnapshotById(id));
  }

  @Override
  public AffectedSublinesModel checkAffectedSublines(Long id, UpdateLineVersionModelV2 newVersion) {
    LineVersion lineVersion = lineService.getLineVersionById(id);
    LineVersion editedVersion = LineMapper.toEntityFromUpdate(newVersion, lineVersion);
    return sublineShorteningService.checkAffectedSublines(lineVersion, editedVersion);
  }

}
