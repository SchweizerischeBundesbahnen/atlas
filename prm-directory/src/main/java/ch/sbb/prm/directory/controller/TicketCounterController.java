package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.prm.model.ticketcounter.CreateTicketCounterVersionModel;
import ch.sbb.atlas.api.prm.model.ticketcounter.ReadTicketCounterVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepoint.SharedServicePointVersionModel;
import ch.sbb.prm.directory.api.TicketCounterApiV1;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
import ch.sbb.prm.directory.mapper.TicketCounterVersionMapper;
import ch.sbb.prm.directory.service.SharedServicePointService;
import ch.sbb.prm.directory.service.TicketCounterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TicketCounterController implements TicketCounterApiV1 {

  private final TicketCounterService ticketCounterService;
  private final SharedServicePointService sharedServicePointService;

  @Override
  public List<ReadTicketCounterVersionModel> getTicketCounters() {
    return ticketCounterService.getAllTicketCounters().stream().map(TicketCounterVersionMapper::toModel).toList();
  }

  @Override
  public ReadTicketCounterVersionModel createTicketCounter(CreateTicketCounterVersionModel ticketCounterVersionModel) {
    SharedServicePointVersionModel sharedServicePointVersionModel = sharedServicePointService.findServicePoint(ticketCounterVersionModel.getParentServicePointSloid()).orElseThrow();
    TicketCounterVersion ticketCounterVersion = ticketCounterService.createTicketCounter(
        TicketCounterVersionMapper.toEntity(ticketCounterVersionModel), sharedServicePointVersionModel);
    return TicketCounterVersionMapper.toModel(ticketCounterVersion);
  }

  @Override
  public List<ReadTicketCounterVersionModel> updateTicketCounter(Long id, CreateTicketCounterVersionModel model) {
    TicketCounterVersion ticketCounterVersionToUpdate =
        ticketCounterService.getTicketCounterVersionById(id).orElseThrow(() -> new IdNotFoundException(id));
    SharedServicePointVersionModel sharedServicePointVersionModel = sharedServicePointService.findServicePoint(model.getParentServicePointSloid()).orElseThrow();
    TicketCounterVersion editedVersion = TicketCounterVersionMapper.toEntity(model);
    ticketCounterService.updateTicketCounterVersion(ticketCounterVersionToUpdate, editedVersion, sharedServicePointVersionModel);

    return ticketCounterService.findAllByNumberOrderByValidFrom(ticketCounterVersionToUpdate.getNumber()).stream()
        .map(TicketCounterVersionMapper::toModel).toList();
  }

}
