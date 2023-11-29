package ch.sbb.prm.directory.service;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.TICKET_COUNTER;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.TicketCounterRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TicketCounterService extends PrmRelatableVersionableService<TicketCounterVersion> {

  private final TicketCounterRepository ticketCounterRepository;

  public TicketCounterService(TicketCounterRepository ticketCounterRepository, StopPointService stopPointService,
      RelationService relationService, ReferencePointRepository referencePointRepository, VersionableService versionableService) {
    super(versionableService, stopPointService, relationService, referencePointRepository);
    this.ticketCounterRepository = ticketCounterRepository;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return TICKET_COUNTER;
  }

  @Override
  protected void incrementVersion(String sloid) {
    ticketCounterRepository.incrementVersion(sloid);
  }

  @Override
  protected TicketCounterVersion save(TicketCounterVersion version) {
    return ticketCounterRepository.saveAndFlush(version);
  }

  @Override
  public List<TicketCounterVersion> getAllVersions(String sloid) {
    return ticketCounterRepository.findAllBySloidOrderByValidFrom(sloid);
  }

  @Override
  protected void applyVersioning(List<VersionedObject> versionedObjects) {
    versionableService.applyVersioning(TicketCounterVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(ticketCounterRepository));
  }

  public List<TicketCounterVersion> getAllTicketCounters() {
    return ticketCounterRepository.findAll();
  }

  @PreAuthorize("@prmUserAdministrationService.hasUserRightsToCreateOrEditPrmObject(#version)")
  public TicketCounterVersion createTicketCounter(TicketCounterVersion version) {
    createRelation(version);
    return save(version);
  }

  @PreAuthorize("@prmUserAdministrationService.hasUserRightsToCreateOrEditPrmObject(#editedVersion)")
  public TicketCounterVersion updateTicketCounterVersion(TicketCounterVersion currentVersion,
                                                         TicketCounterVersion editedVersion) {
    return updateVersion(currentVersion, editedVersion);
  }

  public Optional<TicketCounterVersion> getTicketCounterVersionById(Long id) {
    return ticketCounterRepository.findById(id);
  }
}
