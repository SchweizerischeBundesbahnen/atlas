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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TicketCounterService extends PrmRelatableVersionableService<TicketCounterVersion> {

  private final TicketCounterRepository ticketCounterRepository;
  private final VersionableService versionableService;

  public TicketCounterService(TicketCounterRepository ticketCounterRepository, StopPlaceService stopPlaceRepository,
      RelationService relationService, ReferencePointRepository referencePointRepository, VersionableService versionableService) {
    super(versionableService,stopPlaceRepository,relationService,referencePointRepository);
    this.ticketCounterRepository = ticketCounterRepository;
    this.versionableService = versionableService;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return TICKET_COUNTER;
  }

  @Override
  protected void incrementVersion(ServicePointNumber servicePointNumber) {
    ticketCounterRepository.incrementVersion(servicePointNumber);
  }

  @Override
  protected TicketCounterVersion save(TicketCounterVersion version) {
    return ticketCounterRepository.saveAndFlush(version);
  }

  @Override
  protected List<TicketCounterVersion> getAllVersions(ServicePointNumber servicePointNumber) {
    return this.findAllByNumberOrderByValidFrom(servicePointNumber);
  }

  @Override
  protected void applyVersioning(List<VersionedObject> versionedObjects) {
    versionableService.applyVersioning(TicketCounterVersion.class, versionedObjects,this::save,
        new ApplyVersioningDeleteByIdLongConsumer(ticketCounterRepository));
  }

  public List<TicketCounterVersion> getAllTicketCounters() {
    return ticketCounterRepository.findAll();
  }

  public TicketCounterVersion createTicketCounter(TicketCounterVersion version) {
    createRelation(version);
    return save(version);
  }

  public TicketCounterVersion updateTicketCounterVersion(TicketCounterVersion currentVersion, TicketCounterVersion editedVersion){
    return updateVersion(currentVersion,editedVersion);
  }

  public List<TicketCounterVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return ticketCounterRepository.findAllByNumberOrderByValidFrom(number);
  }

  public Optional<TicketCounterVersion> getTicketCounterVersionById(Long id) {
    return ticketCounterRepository.findById(id);
  }
}
