package ch.sbb.prm.directory.controller;

import ch.sbb.prm.directory.api.TicketCounterApiV1;
import ch.sbb.prm.directory.controller.model.create.CreateTicketCounterVersionModel;
import ch.sbb.prm.directory.controller.model.read.ReadTicketCounterVersionModel;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
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
  public List<ReadTicketCounterVersionModel> getTicketCounters() {
    return toiletService.getAllTicketCounters().stream().map(TicketCounterVersionMapper::toModel).sorted().toList();
  }

  @Override
  public ReadTicketCounterVersionModel createStopPlace(CreateTicketCounterVersionModel ticketCounterVersionModel) {
    TicketCounterVersion ticketCounterVersion = toiletService.createTicketCounter(
        TicketCounterVersionMapper.toEntity(ticketCounterVersionModel));
    return TicketCounterVersionMapper.toModel(ticketCounterVersion);
  }
}
