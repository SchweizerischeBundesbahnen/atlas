package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.PLATFORM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPlaceTestData;
import ch.sbb.prm.directory.TicketCounterTestData;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import ch.sbb.prm.directory.repository.TicketCounterRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class TicketCounterServiceTest {

  private final TicketCounterService ticketCounterService;
  private final TicketCounterRepository ticketCounterRepository;
  private final StopPlaceRepository stopPlaceRepository;

  private final RelationRepository relationRepository;
  private final ReferencePointRepository referencePointRepository;

  @Autowired
  TicketCounterServiceTest(TicketCounterService ticketCounterService, TicketCounterRepository ticketCounterRepository,
      StopPlaceRepository stopPlaceRepository, RelationRepository relationRepository,
      ReferencePointRepository referencePointRepository) {
    this.ticketCounterService = ticketCounterService;
    this.ticketCounterRepository = ticketCounterRepository;
    this.stopPlaceRepository = stopPlaceRepository;
    this.relationRepository = relationRepository;
    this.referencePointRepository = referencePointRepository;
  }

  @Test
  void shouldNotCreateTicketCounterWhenStopPlaceDoesNotExists() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    TicketCounterVersion ticketCounterVersion = TicketCounterTestData.getTicketCounterVersion();
    ticketCounterVersion.setParentServicePointSloid(parentServicePointSloid);

    //when & then
    String message = assertThrows(IllegalStateException.class,
        () -> ticketCounterService.createTicketCounter(ticketCounterVersion)).getLocalizedMessage();
    assertThat(message).isEqualTo("StopPlace with sloid [ch:1:sloid:70000] does not exists!");
  }

  @Test
  void shouldCreateTicketCounterWhenNoReferencePointExists() {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(parentServicePointSloid);
    stopPlaceRepository.save(stopPlaceVersion);
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
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(parentServicePointSloid);
    stopPlaceRepository.save(stopPlaceVersion);
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
    assertThat(relationVersions.get(0).getReferencePointElementType()).isEqualTo(PLATFORM);

  }

}