package ch.sbb.prm.directory.service;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.TICKET_COUNTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.TicketCounterTestData;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistsException;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.repository.TicketCounterRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class TicketCounterServiceTest {

  private final TicketCounterService ticketCounterService;
  private final TicketCounterRepository ticketCounterRepository;
  private final StopPointRepository stopPointRepository;

  private final RelationRepository relationRepository;
  private final ReferencePointRepository referencePointRepository;

  @Autowired
  TicketCounterServiceTest(TicketCounterService ticketCounterService, TicketCounterRepository ticketCounterRepository,
      StopPointRepository stopPointRepository, RelationRepository relationRepository,
      ReferencePointRepository referencePointRepository) {
    this.ticketCounterService = ticketCounterService;
    this.ticketCounterRepository = ticketCounterRepository;
    this.stopPointRepository = stopPointRepository;
    this.relationRepository = relationRepository;
    this.referencePointRepository = referencePointRepository;
  }

  @Test
  void shouldNotCreateTicketCounterWhenStopPointDoesNotExists() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    TicketCounterVersion ticketCounterVersion = TicketCounterTestData.getTicketCounterVersion();
    ticketCounterVersion.setParentServicePointSloid(parentServicePointSloid);

    //when & then
    assertThrows(StopPointDoesNotExistsException.class,
        () -> ticketCounterService.createTicketCounter(ticketCounterVersion)).getLocalizedMessage();
  }

  @Test
  void shouldCreateTicketCounterWhenNoReferencePointExists() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointRepository.save(stopPointVersion);
    TicketCounterVersion ticketCounterVersion = TicketCounterTestData.getTicketCounterVersion();
    ticketCounterVersion.setParentServicePointSloid(parentServicePointSloid);

    //when
    ticketCounterService.createTicketCounter(ticketCounterVersion);

    //then
    List<TicketCounterVersion> ticketCounterVersions = ticketCounterRepository.findByParentServicePointSloid(
        parentServicePointSloid);
    assertThat(ticketCounterVersions).hasSize(1);
    assertThat(ticketCounterVersions.get(0).getParentServicePointSloid()).isEqualTo(
        ticketCounterVersion.getParentServicePointSloid());

    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(
        parentServicePointSloid);
    assertThat(relationVersions).isEmpty();

  }
  @Test
  void shouldCreateTicketCounterWhenReferencePointExists() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);
    TicketCounterVersion ticketCounterVersion = TicketCounterTestData.getTicketCounterVersion();
    ticketCounterVersion.setParentServicePointSloid(parentServicePointSloid);

    //when
    ticketCounterService.createTicketCounter(ticketCounterVersion);

    //then
    List<TicketCounterVersion> ticketCounterVersions = ticketCounterRepository.findByParentServicePointSloid(
        parentServicePointSloid);
    assertThat(ticketCounterVersions).hasSize(1);
    assertThat(ticketCounterVersions.get(0).getParentServicePointSloid()).isEqualTo(
        ticketCounterVersion.getParentServicePointSloid());

    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(
        parentServicePointSloid);
    assertThat(relationVersions).hasSize(1);
    assertThat(relationVersions.get(0).getParentServicePointSloid()).isEqualTo(parentServicePointSloid);
    assertThat(relationVersions.get(0).getReferencePointElementType()).isEqualTo(TICKET_COUNTER);

  }
  @Test
  void shouldNotCreateTicketCounterRelationWhenStopPointIsReduced() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(parentServicePointSloid);
    stopPointVersion.setMeansOfTransport(Set.of(MeanOfTransport.BUS));
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);
    TicketCounterVersion ticketCounterVersion = TicketCounterTestData.getTicketCounterVersion();
    ticketCounterVersion.setParentServicePointSloid(parentServicePointSloid);

    //when
    ticketCounterService.createTicketCounter(ticketCounterVersion);

    //then
    List<TicketCounterVersion> ticketCounterVersions = ticketCounterRepository.findByParentServicePointSloid(
        parentServicePointSloid);
    assertThat(ticketCounterVersions).hasSize(1);
    assertThat(ticketCounterVersions.get(0).getParentServicePointSloid()).isEqualTo(
        ticketCounterVersion.getParentServicePointSloid());

    List<RelationVersion> relationVersions = relationRepository.findAllByParentServicePointSloid(
        parentServicePointSloid);
    assertThat(relationVersions).isEmpty();

  }

}