package ch.sbb.prm.directory.service;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.ReducedVariantException;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistException;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.search.StopPointSearchRestrictions;
import ch.sbb.prm.directory.validation.StopPointValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class StopPointService extends PrmVersionableService<StopPointVersion> {

  private final StopPointRepository stopPointRepository;
  private final StopPointValidationService stopPointValidationService;

  public StopPointService(StopPointRepository stopPointRepository,
                          VersionableService versionableService,
                          StopPointValidationService stopPointValidationService) {
    super(versionableService);
    this.stopPointRepository = stopPointRepository;
    this.stopPointValidationService = stopPointValidationService;
  }

  @Override
  protected void incrementVersion(ServicePointNumber servicePointNumber) {
    stopPointRepository.incrementVersion(servicePointNumber);
  }

  @Override
  @PreAuthorize("""
      @prmUserAdministrationService.hasUserPermissionsForBusinessOrganisations
      (#version, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).PRM)""")
  public StopPointVersion save(StopPointVersion version) {
    stopPointValidationService.validateStopPointRecordingVariants(version);
    return stopPointRepository.saveAndFlush(version);
  }

  @Override
  protected List<StopPointVersion> getAllVersions(ServicePointNumber servicePointNumber) {
    return this.findAllByNumberOrderByValidFrom(servicePointNumber);
  }

  @Override
  protected void applyVersioning(List<VersionedObject> versionedObjects) {
    versionableService.applyVersioning(StopPointVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(stopPointRepository));
  }

  public List<StopPointVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return stopPointRepository.findAllByNumberOrderByValidFrom(number);
  }

  boolean isReduced(String servicePointSloid){
    StopPointVersion parentServicePoint = findAllBySloid(servicePointSloid).stream().findFirst()
            .orElseThrow(() -> new StopPointDoesNotExistException(servicePointSloid));
    return parentServicePoint.isReduced();
  }

  void validateIsNotReduced(String servicePointSloid){
    if(isReduced(servicePointSloid)){
      throw new ReducedVariantException();
    }
  }

  public List<StopPointVersion> findAllBySloid(String sloid){
    return stopPointRepository.findAllBySloid(sloid);
  }

  public Optional<StopPointVersion> getStopPointById(Long id) {
    return stopPointRepository.findById(id);
  }

  public void checkStopPointExists(String sloid) {
    if (!stopPointRepository.existsBySloid(sloid)) {
      throw new StopPointDoesNotExistException(sloid);
    }
  }

  public boolean isStopPointExisting(String sloid) {
    return stopPointRepository.existsBySloid(sloid);
  }

  @PreAuthorize("""
      @prmUserAdministrationService.hasUserPermissionsForBusinessOrganisations
      (#editedVersion, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).PRM)""")
  public StopPointVersion updateStopPointVersion(StopPointVersion currentVersion,
                                                 StopPointVersion editedVersion) {
    stopPointValidationService.validateMeansOfTransportChanging(currentVersion,editedVersion);
    return updateVersion(currentVersion, editedVersion);
  }

  public Page<StopPointVersion> findAll(StopPointSearchRestrictions searchRestrictions) {
    return stopPointRepository.findAll(searchRestrictions.getSpecification(),searchRestrictions.getPageable());
  }
}
