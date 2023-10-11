package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.api.TicketCounterApiV1;
import ch.sbb.prm.directory.controller.model.ticketcounter.CreateTicketCounterVersionModel;
import ch.sbb.prm.directory.controller.model.ticketcounter.ReadTicketCounterVersionModel;
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

  private final TicketCounterService ticketCounterService;

  @Override
  public List<ReadTicketCounterVersionModel> getTicketCounters() {
    return ticketCounterService.getAllTicketCounters().stream().map(TicketCounterVersionMapper::toModel).sorted().toList();
  }

  @Override
  public ReadTicketCounterVersionModel createTicketCounter(CreateTicketCounterVersionModel ticketCounterVersionModel) {
    TicketCounterVersion ticketCounterVersion = ticketCounterService.createTicketCounter(
        TicketCounterVersionMapper.toEntity(ticketCounterVersionModel));
    return TicketCounterVersionMapper.toModel(ticketCounterVersion);
  }

  @Override
  public List<ReadTicketCounterVersionModel> updateTicketCounter(Long id, CreateTicketCounterVersionModel model) {
    TicketCounterVersion parkingLotVersion =
        ticketCounterService.getTicketCounterVersionById(id).orElseThrow(() -> new IdNotFoundException(id));

    TicketCounterVersion editedVersion = TicketCounterVersionMapper.toEntity(model);
    ticketCounterService.updateTicketCounterVersion(parkingLotVersion, editedVersion);

    return ticketCounterService.findAllByNumberOrderByValidFrom(parkingLotVersion.getNumber()).stream()
        .map(TicketCounterVersionMapper::toModel).toList();
  }

}
