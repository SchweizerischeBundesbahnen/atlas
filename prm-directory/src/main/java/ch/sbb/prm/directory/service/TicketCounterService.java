package ch.sbb.prm.directory.service;

import ch.sbb.prm.directory.entity.TicketCounterVersion;
import ch.sbb.prm.directory.repository.TicketCounterRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class TicketCounterService {

  private final TicketCounterRepository ticketCounterRepository;

  public List<TicketCounterVersion> getAllTicketCounters() {
   return ticketCounterRepository.findAll();
  }

  public void createTicketCounter(TicketCounterVersion version){
    ticketCounterRepository.save(version);
  }

  public List<TicketCounterVersion> getByServicePointParentSloid(String parentServicePointSloid){
    return ticketCounterRepository.findByParentServicePointSloid(parentServicePointSloid);
  }

}
