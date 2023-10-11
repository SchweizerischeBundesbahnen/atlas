package ch.sbb.prm.directory.api;

import ch.sbb.prm.directory.controller.model.ticketcounter.CreateTicketCounterVersionModel;
import ch.sbb.prm.directory.controller.model.ticketcounter.ReadTicketCounterVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "PRM - Person with Reduced Mobility")
@RequestMapping("v1/ticket-counters")
public interface TicketCounterApiV1 {

  @GetMapping
  List<ReadTicketCounterVersionModel> getTicketCounters();

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadTicketCounterVersionModel createTicketCounter(@RequestBody @Valid CreateTicketCounterVersionModel ticketCounterVersionModel);

  @ResponseStatus(HttpStatus.OK)
  @PostMapping(path = "{id}")
  List<ReadTicketCounterVersionModel> updateTicketCounter(@PathVariable Long id,
      @RequestBody @Valid CreateTicketCounterVersionModel model);

}
