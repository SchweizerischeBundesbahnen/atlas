package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.TICKET_COUNTER;

import ch.sbb.prm.directory.entity.TicketCounterVersion;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import ch.sbb.prm.directory.repository.TicketCounterRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TicketCounterService extends BaseRelationConnectionService<TicketCounterVersion> {

  private final TicketCounterRepository ticketCounterRepository;

  public TicketCounterService(TicketCounterRepository ticketCounterRepository, StopPlaceRepository stopPlaceRepository,
      RelationRepository relationRepository, ReferencePointRepository referencePointRepository) {
    super(stopPlaceRepository,relationRepository, referencePointRepository);
    this.ticketCounterRepository = ticketCounterRepository;
  }

  public List<TicketCounterVersion> getAllTicketCounters() {
    return ticketCounterRepository.findAll();
  }

  public void createTicketCounter(TicketCounterVersion version) {
    checkStopPlaceExists(version.getParentServicePointSloid());
    createRelation(version, TICKET_COUNTER);
    ticketCounterRepository.save(version);
  }

}
