package ch.sbb.prm.directory.api;

import ch.sbb.prm.directory.controller.model.TicketCounterVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "TicketCounter")
@RequestMapping("v1/ticket-counters")
public interface TicketCounterApiV1 {

  @GetMapping
  List<TicketCounterVersionModel> getTicketCounters();

}
