package ch.sbb.prm.directory.module.stoppoint.controller;

import static ch.sbb.prm.directory.util.PrmVariantUtil.isPrmVariantChanging;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.model.stoppoint.ReadStopPointVersionModel;
import ch.sbb.atlas.api.prm.model.stoppoint.StopPointVersionModel;
import ch.sbb.atlas.helper.TerminationHelper;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.prm.directory.module.platform.service.PlatformService;
import ch.sbb.prm.directory.module.stoppoint.api.StopPointApiV1;
import ch.sbb.prm.directory.module.stoppoint.controller.model.StopPointRequestParams;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import ch.sbb.prm.directory.module.stoppoint.exception.StopPointAlreadyExistsException;
import ch.sbb.prm.directory.module.stoppoint.mapper.StopPointVersionMapper;
import ch.sbb.prm.directory.module.stoppoint.search.StopPointSearchRestrictions;
import ch.sbb.prm.directory.module.stoppoint.service.PrmChangeRecordingVariantService;
import ch.sbb.prm.directory.module.stoppoint.service.StopPointService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StopPointApiV1Controller implements StopPointApiV1 {

  private final StopPointService stopPointService;
  private final PlatformService platformService;
  private final PrmChangeRecordingVariantService prmChangeRecordingVariantService;

  @Override
  public Container<ReadStopPointVersionModel> getStopPoints(Pageable pageable,
      StopPointRequestParams stopPointRequestParams) {
    StopPointSearchRestrictions searchRestrictions = StopPointSearchRestrictions.builder()
        .pageable(pageable)
        .stopPointRequestParams(stopPointRequestParams)
        .build();
    Page<StopPointVersion> stopPointVersions = stopPointService.findAll(searchRestrictions);

    return Container.<ReadStopPointVersionModel>builder()
        .objects(stopPointVersions.stream().map(StopPointVersionMapper::toModel).toList())
        .totalCount(stopPointVersions.getTotalElements())
        .build();
  }

  @Override
  public List<ReadStopPointVersionModel> getStopPointVersions(String sloid) {
    return stopPointService.findAllBySloidOrderByValidFrom(sloid).stream()
        .map(StopPointVersionMapper::toModel)
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
    return StopPointVersionMapper.toModel(savedVersion);
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
        .map(StopPointVersionMapper::toModel).toList();
  }

  @Override
  public List<ReadStopPointVersionModel> terminateStopPoint(String sloid, LocalDate validTo) {
    List<StopPointVersion> currentVersions = stopPointService.findAllBySloidOrderByValidFrom(sloid);

    if (currentVersions.isEmpty()) {
      throw new SloidNotFoundException(sloid);
    }

    StopPointVersion latestVersion = currentVersions.getLast();
    StopPointVersion editedVersion = latestVersion.toBuilder().build();
    editedVersion.setValidTo(validTo);

    terminate(latestVersion, editedVersion);

    return stopPointService.findAllBySloidOrderByValidFrom(sloid).stream().map(StopPointVersionMapper::toModel).toList();
  }

  private void terminate(StopPointVersion latestVersion, StopPointVersion editedVersion) {
    DateRange dateRange = new DateRange(latestVersion.getValidFrom(), latestVersion.getValidTo());

    TerminationHelper.isValidToInLastVersionRange(latestVersion.getSloid(), dateRange, editedVersion.getValidTo());
    stopPointService.updateStopPointVersion(latestVersion, editedVersion);
  }
}
