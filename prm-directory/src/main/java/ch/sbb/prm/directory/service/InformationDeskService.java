package ch.sbb.prm.directory.service;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.INFORMATION_DESK;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.InformationDeskRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InformationDeskService extends PrmRelatableVersionableService<InformationDeskVersion> {

  private final InformationDeskRepository informationDeskRepository;

  public InformationDeskService(InformationDeskRepository informationDeskRepository, StopPlaceService stopPlaceService,
      RelationService relationRepository, ReferencePointRepository referencePointRepository,
      VersionableService versionableService) {
    super(versionableService, stopPlaceService, relationRepository, referencePointRepository);
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

  public InformationDeskVersion createInformationDesk(InformationDeskVersion version) {
    createRelation(version);
    return save(version);
  }

  public InformationDeskVersion updateInformationDeskVersion(InformationDeskVersion currentVersion,
      InformationDeskVersion editedVersion) {
    return updateVersion(currentVersion, editedVersion);
  }

  public Optional<InformationDeskVersion> getInformationDeskVersionById(Long id) {
    return informationDeskRepository.findById(id);
  }

  public List<InformationDeskVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return informationDeskRepository.findAllByNumberOrderByValidFrom(number);
  }
}
