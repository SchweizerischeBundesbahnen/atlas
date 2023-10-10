package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.TICKET_COUNTER;

import ch.sbb.prm.directory.entity.TicketCounterVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.TicketCounterRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TicketCounterService extends RelatableService<TicketCounterVersion> {

  private final TicketCounterRepository ticketCounterRepository;

  public TicketCounterService(TicketCounterRepository ticketCounterRepository, StopPlaceService stopPlaceRepository,
      RelationService relationService, ReferencePointRepository referencePointRepository) {
    super(stopPlaceRepository,relationService, referencePointRepository);
    this.ticketCounterRepository = ticketCounterRepository;
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
    return ticketCounterRepository.saveAndFlush(version);
  }

}
