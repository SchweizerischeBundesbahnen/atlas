package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.TOILET;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import java.util.List;
import java.util.Optional;
import org.hibernate.StaleObjectStateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ToiletService extends RelatableService<ToiletVersion> {

  private final ToiletRepository toiletRepository;

  private final VersionableService versionableService;

  public ToiletService(ToiletRepository toiletRepository, StopPlaceService stopPlaceService,
      RelationService relationService, ReferencePointRepository referencePointRepository, VersionableService versionableService) {
    super(stopPlaceService,relationService,referencePointRepository);
    this.toiletRepository = toiletRepository;
    this.versionableService = versionableService;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return TOILET;
  }

  public List<ToiletVersion> getAllToilets() {
   return toiletRepository.findAll();
  }

  public ToiletVersion createToilet(ToiletVersion version) {
    createRelation(version);
    return save(version);
  }

  public ToiletVersion updateToiletVersion(ToiletVersion currentVersion, ToiletVersion editedVersion){
    checkStaleObjectIntegrity(currentVersion, editedVersion);
    editedVersion.setSloid(currentVersion.getSloid());
    editedVersion.setNumber(currentVersion.getNumber());
    List<ToiletVersion> existingDbVersions = toiletRepository.findAllByNumberOrderByValidFrom(
        currentVersion.getNumber());
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion, existingDbVersions);
    versionableService.applyVersioning(ToiletVersion.class, versionedObjects,
        this::save, new ApplyVersioningDeleteByIdLongConsumer(toiletRepository));
    return currentVersion;
  }

  private ToiletVersion save(ToiletVersion version) {
    return toiletRepository.saveAndFlush(version);
  }
  private void checkStaleObjectIntegrity(ToiletVersion currentVersion, ToiletVersion editedVersion) {
    toiletRepository.incrementVersion(currentVersion.getNumber());
    if (editedVersion.getVersion() != null && !currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(ToiletVersion.class.getSimpleName(), "version");
    }
  }

  public List<ToiletVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return toiletRepository.findAllByNumberOrderByValidFrom(number);
  }

  public Optional<ToiletVersion> getTicketCounterVersionById(Long id) {
    return toiletRepository.findById(id);
  }
}
