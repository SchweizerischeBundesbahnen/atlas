package ch.sbb.prm.directory.api;

import ch.sbb.atlas.api.prm.model.ticketcounter.ReadTicketCounterVersionModel;
import ch.sbb.atlas.api.prm.model.ticketcounter.TicketCounterVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Person with Reduced Mobility")
@RequestMapping("v1/ticket-counters")
public interface TicketCounterApiV1 {

  @GetMapping
  List<ReadTicketCounterVersionModel> getTicketCounters();

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadTicketCounterVersionModel createTicketCounter(
      @RequestBody @Valid TicketCounterVersionModel ticketCounterVersionModel);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadTicketCounterVersionModel> updateTicketCounter(@PathVariable Long id,
      @RequestBody @Valid TicketCounterVersionModel model);

}
