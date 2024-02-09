package ch.sbb.atlas.location.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.servicepoint.Country;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

class SloidRepositoryTest {

  @Mock
  private NamedParameterJdbcTemplate locationJdbcTemplate;

  private SloidRepository sloidRepository;

  @BeforeEach
  void before() {
    MockitoAnnotations.openMocks(this);
    this.sloidRepository = new SloidRepository(locationJdbcTemplate);
  }

  @Test
  void shouldGetAllocatedSloid() {
    // given
    when(locationJdbcTemplate.query(eq("select distinct sloid from allocated_sloid where sloid is not null and sloidtype = "
        + ":sloidType;"), argThat(new ArgumentMatcher<MapSqlParameterSource>() {
      @Override
      public boolean matches(MapSqlParameterSource map) {
        return map.getValue("sloidType").equals("TOILET");
      }
    }), any(RowMapper.class))).thenReturn(List.of("ch:1:sloid:1"));
    // when
    Set<String> allocatedSloids = sloidRepository.getAllocatedSloids(SloidType.TOILET);
    // then
    assertThat(allocatedSloids).hasSize(1);
  }

  @Test
  void shouldGetNextSeqValue() {
    // given
    when(locationJdbcTemplate.queryForObject(eq("select nextval(:sequence);"),
        argThat(new ArgumentMatcher<MapSqlParameterSource>() {
          @Override
          public boolean matches(MapSqlParameterSource map) {
            return map.getValue("sequence").equals("area_seq");
          }
        }), any(RowMapper.class))).thenReturn(100);
    // when
    Integer nextSeqValue = sloidRepository.getNextSeqValue(SloidType.AREA);
    // then
    assertThat(nextSeqValue).isEqualTo(100);
  }

  @Test
  void shouldInsertSloid() {
    // when
    sloidRepository.insertSloid("ch:1:sloid:7000:500", SloidType.PARKING_LOT);
    // then
    verify(locationJdbcTemplate, times(1)).update(eq("insert into allocated_sloid (sloid, sloidtype) values (:sloid, "
        + ":sloidType);"), argThat(new ArgumentMatcher<MapSqlParameterSource>() {
      @Override
      public boolean matches(MapSqlParameterSource map) {
        return map.getValue("sloid").equals("ch:1:sloid:7000:500") && map.getValue("sloidType").equals("PARKING_LOT");
      }
    }));
  }

  @Test
  void shouldGetNextAvailableSloid() {
    // given
    when(locationJdbcTemplate.queryForObject(
        eq("select sloid from available_service_point_sloid where country = :country and claimed = false limit 1;"),
        argThat(new ArgumentMatcher<MapSqlParameterSource>() {
          @Override
          public boolean matches(MapSqlParameterSource map) {
            return map.getValue("country").equals("SWITZERLAND");
          }
        }), any(RowMapper.class))).thenReturn("ch:1:sloid:7000");
    // when
    String nextAvailableSloid = sloidRepository.getNextAvailableSloid(Country.SWITZERLAND);
    // then
    assertThat(nextAvailableSloid).isEqualTo("ch:1:sloid:7000");
  }

  @Test
  void shouldReturnTrueWhenSloidIsAllocated() {
    // given
    when(locationJdbcTemplate.queryForObject(eq("select count(*) from allocated_sloid where sloid = :sloid;"),
        argThat(new ArgumentMatcher<MapSqlParameterSource>() {
          @Override
          public boolean matches(MapSqlParameterSource map) {
            return map.getValue("sloid").equals("ch:1:sloid:7000:500");
          }
        }), any(RowMapper.class))).thenReturn((byte) 1);
    // when
    boolean isSloidAllocated = sloidRepository.isSloidAllocated("ch:1:sloid:7000:500");
    // then
    assertThat(isSloidAllocated).isTrue();
  }

  @Test
  void shouldReturnTrueWhenServicePointSloidIsAvailable() {
    // given
    when(locationJdbcTemplate.queryForObject(eq("select claimed from available_service_point_sloid where sloid = :sloid;"),
        argThat(new ArgumentMatcher<MapSqlParameterSource>() {
          @Override
          public boolean matches(MapSqlParameterSource map) {
            return map.getValue("sloid").equals("ch:1:sloid:7000");
          }
        }), any(RowMapper.class))).thenReturn(Boolean.FALSE);
    // when
    boolean isAvailable = sloidRepository.isServicePointSloidAvailable("ch:1:sloid:7000");
    // then
    assertThat(isAvailable).isTrue();
  }

  @Test
  void shouldDeleteAllocatedSloids() {
    // when
    sloidRepository.deleteAllocatedSloids(new HashSet<>(List.of("ch:1:sloid:7000:500", "ch:1:sloid:20:700")),
        SloidType.PARKING_LOT);
    // then
    verify(locationJdbcTemplate, times(1)).update(
        eq("delete from allocated_sloid where sloid in (:sloids) and sloidtype = :sloidType;"),
        argThat(new ArgumentMatcher<MapSqlParameterSource>() {
          @Override
          public boolean matches(MapSqlParameterSource map) {
            return map.getValue("sloids").equals(
                new HashSet<>(List.of("ch:1:sloid:7000:500", "ch:1:sloid:20:700"))
            ) && map.getValue("sloidType").equals("PARKING_LOT");
          }
        }));
  }

  @Test
  void shouldSetAvailableSloidsToUnclaimed() {
    // when
    sloidRepository.setAvailableSloidsToUnclaimed(new HashSet<>(List.of("ch:1:sloid:7000:500", "ch:1:sloid:20:700")));
    // then
    verify(locationJdbcTemplate, times(1)).update(
        eq("update available_service_point_sloid set claimed = false where sloid in (:sloids);"),
        argThat(new ArgumentMatcher<MapSqlParameterSource>() {
          @Override
          public boolean matches(MapSqlParameterSource map) {
            return map.getValue("sloids").equals(
                new HashSet<>(List.of("ch:1:sloid:7000:500", "ch:1:sloid:20:700"))
            );
          }
        }));
  }

  @Test
  void shouldSetAvailableSloidToClaimed() {
    // when
    sloidRepository.setAvailableSloidToClaimed("ch:1:sloid:7000");
    // then
    verify(locationJdbcTemplate, times(1)).update(
        eq("update available_service_point_sloid set claimed = true where sloid = :sloid;"),
        argThat(new ArgumentMatcher<MapSqlParameterSource>() {
          @Override
          public boolean matches(MapSqlParameterSource map) {
            return map.getValue("sloid").equals(
                "ch:1:sloid:7000"
            );
          }
        }));
  }

  @Test
  void shouldSetAvailableSloidsToClaimed() {
    // given
    JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    when(locationJdbcTemplate.getJdbcTemplate()).thenReturn(jdbcTemplate);
    // when
    sloidRepository.setAvailableSloidsToClaimed(new HashSet<>(List.of("ch:1:sloid:7000:500", "ch:1:sloid:20:0:900")));
    // then
    verify(jdbcTemplate, times(1)).batchUpdate(
        eq("update available_service_point_sloid set claimed = true where sloid in (?);"),
        any(BatchPreparedStatementSetter.class));
  }

  @Test
  void shouldDeleteAvailableServicePointSloidAlreadyClaimed() {
    // given
    JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    when(locationJdbcTemplate.getJdbcTemplate()).thenReturn(jdbcTemplate);
    // when
    sloidRepository.deleteAvailableServicePointSloidsAlreadyClaimed(
        new HashSet<>(List.of("ch:1:sloid:7000:500", "ch:1:sloid:20:0:900")));
    // then
    verify(jdbcTemplate, times(1)).batchUpdate(
        eq("delete from available_service_point_sloid where sloid in (?) and claimed = true;"),
        any(BatchPreparedStatementSetter.class));
  }

  @Test
  void shouldAddMissingAllocatedSloids() {
    // given
    JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    when(locationJdbcTemplate.getJdbcTemplate()).thenReturn(jdbcTemplate);
    // when
    sloidRepository.addMissingAllocatedSloids(new HashSet<>(List.of("ch:1:sloid:7000:500", "ch:1:sloid:20:0:900")),
        SloidType.PARKING_LOT);
    // then
    verify(jdbcTemplate, times(1)).batchUpdate(
        eq("insert into allocated_sloid (sloid, sloidtype) values (?, ?);"),
        any(BatchPreparedStatementSetter.class));
  }

}
