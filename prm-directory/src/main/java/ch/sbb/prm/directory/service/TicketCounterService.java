package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.TICKET_COUNTER;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.TicketCounterRepository;
import java.util.List;
import java.util.Optional;
import org.hibernate.StaleObjectStateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TicketCounterService extends RelatableService<TicketCounterVersion> {

  private final TicketCounterRepository ticketCounterRepository;
  private final VersionableService versionableService;

  public TicketCounterService(TicketCounterRepository ticketCounterRepository, StopPlaceService stopPlaceRepository,
      RelationService relationService, ReferencePointRepository referencePointRepository, VersionableService versionableService) {
    super(stopPlaceRepository,relationService, referencePointRepository);
    this.ticketCounterRepository = ticketCounterRepository;
    this.versionableService = versionableService;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return TICKET_COUNTER;
  }

  public List<TicketCounterVersion> getAllTicketCounters() {
    return ticketCounterRepository.findAll();
  }

  public TicketCounterVersion createTicketCounter(TicketCounterVersion version) {
    createRelation(version);
    return save(version);
  }

  public TicketCounterVersion updateTicketCounterVersion(TicketCounterVersion currentVersion, TicketCounterVersion editedVersion){
    checkStaleObjectIntegrity(currentVersion, editedVersion);
    editedVersion.setSloid(currentVersion.getSloid());
    editedVersion.setNumber(currentVersion.getNumber());
    List<TicketCounterVersion> existingDbVersions = ticketCounterRepository.findAllByNumberOrderByValidFrom(
        currentVersion.getNumber());
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion, existingDbVersions);
    versionableService.applyVersioning(TicketCounterVersion.class, versionedObjects,
        this::save, new ApplyVersioningDeleteByIdLongConsumer(ticketCounterRepository));
    return currentVersion;
  }

  private TicketCounterVersion save(TicketCounterVersion version) {
    return ticketCounterRepository.saveAndFlush(version);
  }

  private void checkStaleObjectIntegrity(TicketCounterVersion currentVersion, TicketCounterVersion editedVersion) {
    ticketCounterRepository.incrementVersion(currentVersion.getNumber());
    if (editedVersion.getVersion() != null && !currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(TicketCounterVersion.class.getSimpleName(), "version");
    }
  }

  public List<TicketCounterVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return ticketCounterRepository.findAllByNumberOrderByValidFrom(number);
  }

  public Optional<TicketCounterVersion> getTicketCounterVersionById(Long id) {
    return ticketCounterRepository.findById(id);
  }
}
