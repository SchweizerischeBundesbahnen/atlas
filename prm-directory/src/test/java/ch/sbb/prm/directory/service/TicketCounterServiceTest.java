package ch.sbb.prm.directory.service;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.SharedServicePointVersionModel;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.TicketCounterTestData;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistException;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.repository.TicketCounterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.TICKET_COUNTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@IntegrationTest
@Transactional
class TicketCounterServiceTest {

  private static final String PARENT_SERVICE_POINT_SLOID = "ch:1:sloid:70000";
  private static final SharedServicePointVersionModel SHARED_SERVICE_POINT_VERSION_MODEL =
          new SharedServicePointVersionModel(PARENT_SERVICE_POINT_SLOID,
                  Collections.singleton("sboid"),
                  Collections.singleton(""));

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
  void shouldNotCreateTicketCounterWhenStopPointDoesNotExist() {
    //given
    TicketCounterVersion ticketCounterVersion = TicketCounterTestData.getTicketCounterVersion();
    ticketCounterVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when & then
    assertThrows(StopPointDoesNotExistException.class, () -> ticketCounterService
                .createTicketCounter(ticketCounterVersion, SHARED_SERVICE_POINT_VERSION_MODEL)).getLocalizedMessage();
  }

  @Test
  void shouldCreateTicketCounterWhenNoReferencePointExists() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    TicketCounterVersion ticketCounterVersion = TicketCounterTestData.getTicketCounterVersion();
    ticketCounterVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when
    ticketCounterService.createTicketCounter(ticketCounterVersion, SHARED_SERVICE_POINT_VERSION_MODEL);
    //then
    List<TicketCounterVersion> ticketCounterVersions = ticketCounterRepository
            .findByParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    assertThat(ticketCounterVersions).hasSize(1);
    assertThat(ticketCounterVersions.get(0).getParentServicePointSloid()).isEqualTo(
        ticketCounterVersion.getParentServicePointSloid());

    List<RelationVersion> relationVersions = relationRepository
            .findAllByParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    assertThat(relationVersions).isEmpty();
  }

  @Test
  void shouldCreateTicketCounterWhenReferencePointExists() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setSloid(PARENT_SERVICE_POINT_SLOID);
    stopPointRepository.save(stopPointVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    referencePointRepository.save(referencePointVersion);
    TicketCounterVersion ticketCounterVersion = TicketCounterTestData.getTicketCounterVersion();
    ticketCounterVersion.setParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    //when
    ticketCounterService.createTicketCounter(ticketCounterVersion, SHARED_SERVICE_POINT_VERSION_MODEL);
    //then
    List<TicketCounterVersion> ticketCounterVersions = ticketCounterRepository
            .findByParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    assertThat(ticketCounterVersions).hasSize(1);
    assertThat(ticketCounterVersions.get(0).getParentServicePointSloid()).isEqualTo(
        ticketCounterVersion.getParentServicePointSloid());
    List<RelationVersion> relationVersions = relationRepository
            .findAllByParentServicePointSloid(PARENT_SERVICE_POINT_SLOID);
    assertThat(relationVersions).hasSize(1);
    assertThat(relationVersions.get(0).getParentServicePointSloid()).isEqualTo(PARENT_SERVICE_POINT_SLOID);
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
    ticketCounterService.createTicketCounter(ticketCounterVersion, SHARED_SERVICE_POINT_VERSION_MODEL);

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