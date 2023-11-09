package ch.sbb.prm.directory.service;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.SharedServicePointVersionModel;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.TicketCounterRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.TICKET_COUNTER;

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
    versionableService.applyVersioning(TicketCounterVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(ticketCounterRepository));
  }

  public List<TicketCounterVersion> getAllTicketCounters() {
    return ticketCounterRepository.findAll();
  }

  @PreAuthorize("""
      @prmBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisations
      (#sharedServicePointVersionModel, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).PRM)""")
  public TicketCounterVersion createTicketCounter(TicketCounterVersion version,
                                                  SharedServicePointVersionModel sharedServicePointVersionModel) {
    createRelation(version);
    return save(version);
  }

  @PreAuthorize("""
      @prmBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisations
      (#sharedServicePointVersionModel, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).PRM)""")
  public TicketCounterVersion updateTicketCounterVersion(TicketCounterVersion currentVersion,
      TicketCounterVersion editedVersion, SharedServicePointVersionModel sharedServicePointVersionModel) {
    return updateVersion(currentVersion, editedVersion);
  }

  public List<TicketCounterVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number) {
    return ticketCounterRepository.findAllByNumberOrderByValidFrom(number);
  }

  public Optional<TicketCounterVersion> getTicketCounterVersionById(Long id) {
    return ticketCounterRepository.findById(id);
  }
}
