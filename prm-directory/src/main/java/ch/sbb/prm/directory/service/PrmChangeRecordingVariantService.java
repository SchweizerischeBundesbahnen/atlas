package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.util.PrmVariantUtil.isChangingFromCompleteToReduced;
import static ch.sbb.prm.directory.util.PrmVariantUtil.isChangingFromReducedToComplete;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.mapper.PlatformVersionMapper;
import ch.sbb.prm.directory.mapper.StopPointVersionMapper;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class PrmChangeRecordingVariantService {

  private final StopPointRepository stopPointRepository;
  private final PlatformRepository platformRepository;
  private final ReferencePointRepository referencePointRepository;
  private final RelationRepository relationRepository;

  public PrmChangeRecordingVariantService(StopPointRepository stopPointRepository, PlatformRepository platformRepository,
      ReferencePointRepository referencePointRepository, RelationRepository relationRepository) {
    this.stopPointRepository = stopPointRepository;
    this.platformRepository = platformRepository;
    this.referencePointRepository = referencePointRepository;
    this.relationRepository = relationRepository;
  }

  @PreAuthorize("@prmUserAdministrationService.isAtLeastPrmSupervisor()")
  @Transactional
  public StopPointVersion stopPointChangeRecordingVariant(StopPointVersion stopPointVersionToUpdate,
      StopPointVersion editedVersion) {
    if (isChangingFromCompleteToReduced(stopPointVersionToUpdate, editedVersion)) {
      return stopPointChangeFromCompleteToReduced(stopPointVersionToUpdate, editedVersion);
    }
    if (isChangingFromReducedToComplete(stopPointVersionToUpdate, editedVersion)) {
      return changeFromReduceToComplete(stopPointVersionToUpdate, editedVersion);
    }
    throw new IllegalStateException("Record variant must be different!");
  }

  private StopPointVersion stopPointChangeFromCompleteToReduced(StopPointVersion stopPointVersionToUpdate,
      StopPointVersion editedVersion) {
    StopPointVersion stopPointVersion = stopPointChangeRecordingVariant(stopPointVersionToUpdate,
        editedVersion.getMeansOfTransport());
    changePlatformToReduced(stopPointVersionToUpdate, editedVersion.getMeansOfTransport());
    setStatusRevokedToReferencePoints(stopPointVersionToUpdate.getParentServicePointSloid());
    deleteRelations(stopPointVersionToUpdate.getParentServicePointSloid());
    return stopPointVersion;
  }

  private void changePlatformToReduced(StopPointVersion stopPointVersionToUpdate, Set<MeanOfTransport> newMeansOfTransport) {
    platformChangeRecordingVariant(stopPointVersionToUpdate.getParentServicePointSloid(), newMeansOfTransport);
  }

  private StopPointVersion changeFromReduceToComplete(StopPointVersion stopPointVersionToUpdate,
      StopPointVersion editedVersion) {
    StopPointVersion stopPointVersion = stopPointChangeRecordingVariant(stopPointVersionToUpdate,
        editedVersion.getMeansOfTransport());
    changePlatformToComplete(stopPointVersionToUpdate, editedVersion.getMeansOfTransport());
    return stopPointVersion;
  }

  private void changePlatformToComplete(StopPointVersion stopPointVersionToUpdate, Set<MeanOfTransport> newMeansOfTransport) {
    platformChangeRecordingVariant(stopPointVersionToUpdate.getParentServicePointSloid(), newMeansOfTransport);
  }

  StopPointVersion stopPointChangeRecordingVariant(StopPointVersion stopPointVersion, Set<MeanOfTransport> meanOfTransports) {
    List<StopPointVersion> stopPointVersions = stopPointRepository.findAllBySloidOrderByValidFrom(stopPointVersion.getSloid());
    LocalDate validFrom = stopPointVersions.getFirst().getValidFrom();
    LocalDate validTo = stopPointVersions.getLast().getValidTo();
    StopPointVersion changedRecordingVariantStopPointVersion = StopPointVersionMapper.resetToDefaultValue(stopPointVersion,
        validFrom, validTo,
        meanOfTransports);
    stopPointRepository.deleteAllById(stopPointVersions.stream().map(StopPointVersion::getId).collect(Collectors.toSet()));
    stopPointRepository.flush();
    return stopPointRepository.saveAndFlush(changedRecordingVariantStopPointVersion);
  }

  void platformChangeRecordingVariant(String sloid, Set<MeanOfTransport> newMeansOfTransport) {
    List<PlatformVersion> platformVersionsByParentSloid
        = platformRepository.findAllByParentServicePointSloid(sloid);
    Map<String, List<PlatformVersion>> platforms = platformVersionsByParentSloid.stream()
        .collect(Collectors.groupingBy(PlatformVersion::getSloid));

    platforms.forEach((key, platformVersionsGroup) -> {
      if (!platformVersionsGroup.isEmpty()) {
        LocalDate validFrom = platformVersionsGroup.getFirst().getValidFrom();
        LocalDate validTo = platformVersionsGroup.getLast().getValidTo();
        PlatformVersion changedRecordingVariantStopPointVersion = PlatformVersionMapper.resetToDefaultValue(
            platformVersionsGroup.getFirst(),
            validFrom, validTo, newMeansOfTransport);
        platformRepository.deleteAllById(platformVersionsGroup.stream().map(PlatformVersion::getId).collect(Collectors.toSet()));
        platformRepository.flush();
        platformRepository.saveAndFlush(changedRecordingVariantStopPointVersion);
      }

    });

  }

  List<ReferencePointVersion> setStatusRevokedToReferencePoints(String sloid) {
    List<ReferencePointVersion> referencePoints = referencePointRepository.findByParentServicePointSloid(sloid);
    referencePoints.forEach(referencePointVersion -> {
      referencePointVersion.setStatus(Status.REVOKED);
      referencePointRepository.saveAndFlush(referencePointVersion);
    });
    return referencePoints;
  }

  private void deleteRelations(String parentServicePointSloid) {
    List<RelationVersion> relations = relationRepository.findAllByParentServicePointSloid(parentServicePointSloid);
    relationRepository.deleteAll(relations);
  }

}
