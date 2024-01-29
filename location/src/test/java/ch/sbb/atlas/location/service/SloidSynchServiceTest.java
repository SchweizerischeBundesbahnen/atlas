package ch.sbb.atlas.location.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.location.LocationSchemaCreation;
import ch.sbb.atlas.location.PrmDbSchemaCreation;
import ch.sbb.atlas.location.SePoDiSchemaCreation;
import ch.sbb.atlas.location.repository.PrmRepository;
import ch.sbb.atlas.location.repository.SePoDiRepository;
import ch.sbb.atlas.location.repository.SloidRepository;
import ch.sbb.atlas.model.controller.IntegrationTest;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@LocationSchemaCreation
@PrmDbSchemaCreation
@SePoDiSchemaCreation
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class SloidSynchServiceTest {

  @Qualifier("prmJdbcTemplate")
  private final NamedParameterJdbcTemplate prmJdbcTemplate;

  @Qualifier("sePoDiJdbcTemplate")
  private final NamedParameterJdbcTemplate sePoDiJdbcTemplate;

  @Qualifier("locationJdbcTemplate")
  private final NamedParameterJdbcTemplate locationJdbcTemplate;
  private final SloidRepository sloidRepository;

  @MockBean
  private final PrmRepository prmRepository;

  @MockBean
  private final SePoDiRepository sePoDiRepository;

  private final SloidSynchService sloidSynchService;

  @Autowired
  SloidSynchServiceTest(NamedParameterJdbcTemplate prmJdbcTemplate, NamedParameterJdbcTemplate sePoDiJdbcTemplate,
      NamedParameterJdbcTemplate locationJdbcTemplate, SloidRepository sloidRepository, PrmRepository prmRepository,
      SePoDiRepository sePoDiRepository, SloidSynchService sloidSynchService) {
    this.prmJdbcTemplate = prmJdbcTemplate;
    this.sePoDiJdbcTemplate = sePoDiJdbcTemplate;
    this.locationJdbcTemplate = locationJdbcTemplate;
    this.sloidRepository = sloidRepository;
    this.prmRepository = prmRepository;
    this.sePoDiRepository = sePoDiRepository;
    this.sloidSynchService = sloidSynchService;
  }

  @Test
  void shouldSyncServicePointWhenAlreadyDistributedSloidAreMoreThenAllocated() throws SQLException {
    //given
    Set<String> allocatedSloids = Set.of("ch:sloid:1", "ch:sloid:2", "ch:sloid:3");
    Set<String> alreadyDistributedSloids = Set.of("ch:sloid:1", "ch:sloid:2", "ch:sloid:3", "ch:sloid:4");
    allocatedSloids.forEach(s -> sloidRepository.insertSloid(s, SloidType.SERVICE_POINT));
    when(sePoDiRepository.getAlreadyServicePointDistributedSloid()).thenReturn(alreadyDistributedSloids);
    //when
    sloidSynchService.sync();
    //then
    Set<String> result = sloidRepository.getAllocatedSloid(SloidType.SERVICE_POINT);
    assertThat(result)
        .isNotNull()
        .hasSize(4)
        .containsAnyElementsOf(alreadyDistributedSloids);
    Set<String> claimedAvailableSloid = getClaimedAvailableSloid();
    assertThat(claimedAvailableSloid).isNotNull().isEmpty();
  }

  @Test
  void shouldSyncServicePointWhenAllocatedSloidAreMoreThenAlreadyDistributed() throws SQLException {
    //given
    Set<String> allocatedSloids = Set.of("ch:sloid:1", "ch:sloid:2", "ch:sloid:3", "ch:sloid:4");
    Set<String> alreadyDistributedSloids = Set.of("ch:sloid:1", "ch:sloid:2", "ch:sloid:3");
    allocatedSloids.forEach(s -> sloidRepository.insertSloid(s, SloidType.SERVICE_POINT));
    when(sePoDiRepository.getAlreadyServicePointDistributedSloid()).thenReturn(alreadyDistributedSloids);
    //when
    sloidSynchService.sync();
    //then
    Set<String> result = sloidRepository.getAllocatedSloid(SloidType.SERVICE_POINT);
    assertThat(result)
        .isNotNull()
        .hasSize(3)
        .containsAnyElementsOf(alreadyDistributedSloids);
    Set<String> claimedAvailableSloid = getClaimedAvailableSloid();
    assertThat(claimedAvailableSloid).isNotNull().isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = SloidType.class,
          names = {"PLATFORM", "AREA", "REFERENCE_POINT", "PARKING_LOT", "INFO_DESK", "TICKET_COUNTER", "TOILET"})
  void shouldSyncSloidWhenAlreadyDistributedSloidAreMoreThenAllocated(SloidType sloidType) {
    //given
    Set<String> allocatedSloids = Set.of("ch:sloid:1", "ch:sloid:2", "ch:sloid:3");
    Set<String> alreadyDistributedSloids = Set.of("ch:sloid:1", "ch:sloid:2", "ch:sloid:3", "ch:sloid:4");
    allocatedSloids.forEach(s -> sloidRepository.insertSloid(s, sloidType));
    if (SloidType.PLATFORM == sloidType || SloidType.AREA == sloidType) {
      when(sePoDiRepository.getAlreadyDistributedSloid(sloidType)).thenReturn(alreadyDistributedSloids);
    } else {
      when(prmRepository.getAlreadyDistributedSloid(sloidType)).thenReturn(alreadyDistributedSloids);
    }
    //when
    sloidSynchService.sync();
    //then
    Set<String> result = sloidRepository.getAllocatedSloid(sloidType);
    assertThat(result).isNotNull().hasSize(4);
  }

  @ParameterizedTest
  @EnumSource(value = SloidType.class,
          names = {"PLATFORM", "AREA", "REFERENCE_POINT", "PARKING_LOT", "INFO_DESK", "TICKET_COUNTER", "TOILET"})
  void shouldSyncSloidWhenAllocatedAreMoreThenDistributed(SloidType sloidType) {
    //given
    Set<String> allocatedSloids = Set.of("ch:sloid:1", "ch:sloid:2", "ch:sloid:3", "ch:sloid:4");
    Set<String> alreadyDistributedSloids = Set.of("ch:sloid:1", "ch:sloid:2", "ch:sloid:3");
    allocatedSloids.forEach(s -> sloidRepository.insertSloid(s, sloidType));
    if (SloidType.PLATFORM == sloidType || SloidType.AREA == sloidType) {
      when(sePoDiRepository.getAlreadyDistributedSloid(sloidType)).thenReturn(alreadyDistributedSloids);
    } else {
      when(prmRepository.getAlreadyDistributedSloid(sloidType)).thenReturn(alreadyDistributedSloids);
    }
    //when
    sloidSynchService.sync();
    //then
    Set<String> result = sloidRepository.getAllocatedSloid(sloidType);
    assertThat(result).isNotNull().hasSize(3);
  }

  private Set<String> getClaimedAvailableSloid() throws SQLException {
    Objects.requireNonNull(locationJdbcTemplate.getJdbcTemplate().getDataSource()).getConnection().commit();
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    String sqlQuery = "select sloid from available_service_point_sloid where claimed = true";
    return new HashSet<>(locationJdbcTemplate.query(sqlQuery, mapSqlParameterSource, (rs, row) -> rs.getString("sloid")));
  }

}