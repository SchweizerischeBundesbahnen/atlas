package ch.sbb.prm.directory.controller;

import static ch.sbb.prm.directory.util.PrmVariantUtil.isPrmVariantChanging;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.model.stoppoint.ReadStopPointVersionModel;
import ch.sbb.atlas.api.prm.model.stoppoint.RecordingObligationUpdateRequest;
import ch.sbb.atlas.api.prm.model.stoppoint.StopPointVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.api.StopPointApiV1;
import ch.sbb.prm.directory.controller.model.StopPointRequestParams;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.StopPointAlreadyExistsException;
import ch.sbb.prm.directory.mapper.StopPointVersionMapper;
import ch.sbb.prm.directory.search.StopPointSearchRestrictions;
import ch.sbb.prm.directory.service.PlatformService;
import ch.sbb.prm.directory.service.PrmChangeRecordingVariantService;
import ch.sbb.prm.directory.service.RecordingObligationService;
import ch.sbb.prm.directory.service.StopPointService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StopPointController implements StopPointApiV1 {

  private final StopPointService stopPointService;
  private final PlatformService platformService;
  private final RecordingObligationService recordingObligationService;
  private final PrmChangeRecordingVariantService prmChangeRecordingVariantService;

  @Override
  public Container<ReadStopPointVersionModel> getStopPoints(Pageable pageable,
      StopPointRequestParams stopPointRequestParams) {
    StopPointSearchRestrictions searchRestrictions = StopPointSearchRestrictions.builder()
        .pageable(pageable)
        .stopPointRequestParams(stopPointRequestParams)
        .build();
    Page<StopPointVersion> stopPointVersions = stopPointService.findAll(searchRestrictions);
    Map<String, Boolean> recordingObligations = recordingObligationService.getRecordingObligations(
        stopPointVersions.stream().map(StopPointVersion::getSloid).collect(Collectors.toList()));

    return Container.<ReadStopPointVersionModel>builder()
        .objects(stopPointVersions.stream().map(i -> StopPointVersionMapper.toModel(i, recordingObligations)).toList())
        .totalCount(stopPointVersions.getTotalElements())
        .build();
  }

  @Override
  public List<ReadStopPointVersionModel> getStopPointVersions(String sloid) {
    return stopPointService.findAllBySloidOrderByValidFrom(sloid).stream()
        .map(i -> StopPointVersionMapper.toModel(i, recordingObligationService.getRecordingObligation(sloid)))
        .toList();
  }

  @Override
  public ReadStopPointVersionModel createStopPoint(StopPointVersionModel model) {
    boolean stopPointExisting = stopPointService.isStopPointExisting(model.getSloid());
    if (stopPointExisting) {
      throw new StopPointAlreadyExistsException(model.getSloid());
    }
    StopPointVersion stopPointVersion = StopPointVersionMapper.toEntity(model);
    StopPointVersion savedVersion = stopPointService.save(stopPointVersion);
    return StopPointVersionMapper.toModel(savedVersion, true);
  }

  @Override
  public List<ReadStopPointVersionModel> updateStopPoint(Long id, StopPointVersionModel model) {
    StopPointVersion stopPointVersionToUpdate =
        stopPointService.getStopPointById(id).orElseThrow(() -> new IdNotFoundException(id));
    StopPointVersion editedVersion = StopPointVersionMapper.toEntity(model);
    if (isPrmVariantChanging(stopPointVersionToUpdate, editedVersion)) {
      prmChangeRecordingVariantService.stopPointChangeRecordingVariant(stopPointVersionToUpdate, editedVersion);
    } else {
      stopPointService.updateStopPointVersion(stopPointVersionToUpdate, editedVersion);
      platformService.updateAttentionFieldByParentSloid(stopPointVersionToUpdate.getSloid(), editedVersion.getMeansOfTransport());
    }
    return stopPointService.findAllByNumberOrderByValidFrom(stopPointVersionToUpdate.getNumber()).stream()
        .map(i -> StopPointVersionMapper.toModel(i,
            recordingObligationService.getRecordingObligation(stopPointVersionToUpdate.getSloid()))).toList();
  }

  @Override
  public void updateRecordingObligation(String sloid, RecordingObligationUpdateRequest recordingObligationUpdateRequest) {
    recordingObligationService.setRecordingObligation(sloid, recordingObligationUpdateRequest.getValue());
  }

}
