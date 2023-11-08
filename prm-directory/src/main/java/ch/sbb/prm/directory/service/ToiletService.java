package ch.sbb.prm.directory.service;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.SharedServicePointVersionModel;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.TOILET;

@Service
@Transactional
public class ToiletService extends PrmRelatableVersionableService<ToiletVersion> {

  private final ToiletRepository toiletRepository;

  public ToiletService(ToiletRepository toiletRepository, StopPointService stopPointService,
      RelationService relationService, ReferencePointRepository referencePointRepository, VersionableService versionableService) {
    super(versionableService, stopPointService, relationService, referencePointRepository);
    this.toiletRepository = toiletRepository;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return TOILET;
  }

  @Override
  protected void incrementVersion(ServicePointNumber servicePointNumber) {
    this.toiletRepository.incrementVersion(servicePointNumber);
  }

  @Override
  protected ToiletVersion save(ToiletVersion version) {
    return toiletRepository.saveAndFlush(version);
  }

  @Override
  protected List<ToiletVersion> getAllVersions(ServicePointNumber servicePointNumber) {
    return this.findAllByNumberOrderByValidFrom(servicePointNumber);
  }

  @Override
  protected void applyVersioning(List<VersionedObject> versionedObjects) {
    versionableService.applyVersioning(ToiletVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(toiletRepository));
  }

  public List<ToiletVersion> getAllToilets() {
    return toiletRepository.findAll();
  }

  @PreAuthorize("@prmBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisations"
          + "(#sharedServicePointVersionModel, "
          + "T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).PRM)")
  public ToiletVersion createToilet(ToiletVersion version, SharedServicePointVersionModel sharedServicePointVersionModel) {
    createRelation(version);
    return save(version);
  }

  @PreAuthorize("@prmBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisations"
          + "(#sharedServicePointVersionModel, "
          + "T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).PRM)")
  public ToiletVersion updateToiletVersion(ToiletVersion currentVersion, ToiletVersion editedVersion, SharedServicePointVersionModel sharedServicePointVersionModel) {
    return updateVersion(currentVersion, editedVersion);
  }

  public List<ToiletVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return toiletRepository.findAllByNumberOrderByValidFrom(number);
  }

  public Optional<ToiletVersion> getToiletVersionById(Long id) {
    return toiletRepository.findById(id);
  }

}
