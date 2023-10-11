package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.INFORMATION_DESK;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.InformationDeskRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import java.util.List;
import java.util.Optional;
import org.hibernate.StaleObjectStateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InformationDeskService extends RelatableService<InformationDeskVersion> {

  private final InformationDeskRepository informationDeskRepository;
  private final VersionableService versionableService;

  public InformationDeskService(InformationDeskRepository informationDeskRepository, StopPlaceService stopPlaceService,
      RelationService relationRepository, ReferencePointRepository referencePointRepository,
      VersionableService versionableService) {
    super(stopPlaceService,relationRepository,referencePointRepository);
    this.informationDeskRepository = informationDeskRepository;
    this.versionableService = versionableService;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return INFORMATION_DESK;
  }
  public List<InformationDeskVersion> getAllInformationDesks() {
   return informationDeskRepository.findAll();
  }

  public InformationDeskVersion createInformationDesk(InformationDeskVersion version) {
    createRelation(version);
    return save(version);
  }

  public InformationDeskVersion updateInformationDeskVersion(InformationDeskVersion currentVersion, InformationDeskVersion editedVersion){
    checkStaleObjectIntegrity(currentVersion, editedVersion);
    editedVersion.setSloid(currentVersion.getSloid());
    editedVersion.setNumber(currentVersion.getNumber());
    List<InformationDeskVersion> existingDbVersions = informationDeskRepository.findAllByNumberOrderByValidFrom(
        currentVersion.getNumber());
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion, existingDbVersions);
    versionableService.applyVersioning(InformationDeskVersion.class, versionedObjects,
        this::save, new ApplyVersioningDeleteByIdLongConsumer(informationDeskRepository));
    return currentVersion;
  }

  private InformationDeskVersion save(InformationDeskVersion version) {
    return informationDeskRepository.saveAndFlush(version);
  }

  private void checkStaleObjectIntegrity(InformationDeskVersion currentVersion, InformationDeskVersion editedVersion) {
    informationDeskRepository.incrementVersion(currentVersion.getNumber());
    if (editedVersion.getVersion() != null && !currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(InformationDeskVersion.class.getSimpleName(), "version");
    }
  }

  public Optional<InformationDeskVersion> getInformationDeskVersionById(Long id) {
    return informationDeskRepository.findById(id);
  }

  public List<InformationDeskVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return informationDeskRepository.findAllByNumberOrderByValidFrom(number);
  }
}
