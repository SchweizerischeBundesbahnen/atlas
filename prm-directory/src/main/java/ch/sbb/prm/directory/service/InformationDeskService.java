package ch.sbb.prm.directory.service;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.SharedServicePointVersionModel;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.repository.InformationDeskRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.INFORMATION_DESK;

@Service
@Transactional
public class InformationDeskService extends PrmRelatableVersionableService<InformationDeskVersion> {

  private final InformationDeskRepository informationDeskRepository;

  public InformationDeskService(InformationDeskRepository informationDeskRepository, StopPointService stopPointService,
      RelationService relationRepository, ReferencePointRepository referencePointRepository,
      VersionableService versionableService) {
    super(versionableService, stopPointService, relationRepository, referencePointRepository);
    this.informationDeskRepository = informationDeskRepository;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return INFORMATION_DESK;
  }

  @Override
  protected void incrementVersion(ServicePointNumber servicePointNumber) {
    informationDeskRepository.incrementVersion(servicePointNumber);
  }

  @Override
  protected InformationDeskVersion save(InformationDeskVersion version) {
    return informationDeskRepository.saveAndFlush(version);
  }

  @Override
  protected List<InformationDeskVersion> getAllVersions(ServicePointNumber servicePointNumber) {
    return this.findAllByNumberOrderByValidFrom(servicePointNumber);
  }

  @Override
  protected void applyVersioning(List<VersionedObject> versionedObjects) {
    versionableService.applyVersioning(InformationDeskVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(informationDeskRepository));
  }

  public List<InformationDeskVersion> getAllInformationDesks() {
    return informationDeskRepository.findAll();
  }

  @PreAuthorize("@prmBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisations"
                  + "(#sharedServicePointVersionModel, "
                  + "T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).PRM)")
  public InformationDeskVersion createInformationDesk(InformationDeskVersion version, SharedServicePointVersionModel sharedServicePointVersionModel) {
    createRelation(version);
    return save(version);
  }

  @PreAuthorize("@prmBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisations"
          + "(#sharedServicePointVersionModel, "
          + "T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).PRM)")
  public InformationDeskVersion updateInformationDeskVersion(InformationDeskVersion currentVersion,
      InformationDeskVersion editedVersion, SharedServicePointVersionModel sharedServicePointVersionModel) {
    return updateVersion(currentVersion, editedVersion);
  }

  public Optional<InformationDeskVersion> getInformationDeskVersionById(Long id) {
    return informationDeskRepository.findById(id);
  }

  public List<InformationDeskVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return informationDeskRepository.findAllByNumberOrderByValidFrom(number);
  }
}
