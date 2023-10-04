package ch.sbb.prm.directory.controller;

import ch.sbb.prm.directory.api.TicketCounterApiV1;
import ch.sbb.prm.directory.controller.model.TicketCounterVersionModel;
import ch.sbb.prm.directory.mapper.TicketCounterVersionMapper;
import ch.sbb.prm.directory.service.TicketCounterService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TicketCounterController implements TicketCounterApiV1 {

  private final TicketCounterService toiletService;

  @Override
  public List<TicketCounterVersionModel> getTicketCounters() {
    return toiletService.getAllTicketCounters().stream().map(TicketCounterVersionMapper::toModel).sorted().toList();
  }
}
