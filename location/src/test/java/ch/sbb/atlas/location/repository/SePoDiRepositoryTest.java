package ch.sbb.atlas.location.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.location.SloidType;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

class SePoDiRepositoryTest {

  @Mock
  private NamedParameterJdbcTemplate sePoDiJdbcTemplate;

  private SePoDiRepository sePoDiRepository;

  @BeforeEach
  void before() {
    MockitoAnnotations.openMocks(this);
    this.sePoDiRepository = new SePoDiRepository(sePoDiJdbcTemplate);
  }

  @Test
  void shouldGetAlreadyDistributedSloidsForAREA() {
    // given
    when(sePoDiJdbcTemplate.query(eq("""
            select distinct sloid from traffic_point_element_version
            where sloid is not null and traffic_point_element_type = :traffic_point_element_type;
            """),
        argThat(new ArgumentMatcher<MapSqlParameterSource>() {
          @Override
          public boolean matches(MapSqlParameterSource map) {
            return map.getValue("traffic_point_element_type").equals("BOARDING_AREA");
          }
        }),
        any(RowMapper.class))).thenReturn(List.of("ch:1:sloid:1"));
    // when
    Set<String> distributedSloids = sePoDiRepository.getAlreadyDistributedSloids(SloidType.AREA);
    // then
    assertThat(distributedSloids).hasSize(1);
    assertThat(distributedSloids.contains("ch:1:sloid:1")).isTrue();
  }

  @Test
  void shouldGetAlreadyDistributedSloidsForPLATFORM() {
    // given
    when(sePoDiJdbcTemplate.query(eq("""
            select distinct sloid from traffic_point_element_version 
            where sloid is not null and traffic_point_element_type = :traffic_point_element_type;
            """),
        argThat(new ArgumentMatcher<MapSqlParameterSource>() {
          @Override
          public boolean matches(MapSqlParameterSource map) {
            return map.getValue("traffic_point_element_type").equals("BOARDING_PLATFORM");
          }
        }),
        any(RowMapper.class))).thenReturn(List.of("ch:1:sloid:1"));
    // when
    Set<String> distributedSloids = sePoDiRepository.getAlreadyDistributedSloids(SloidType.PLATFORM);
    // then
    assertThat(distributedSloids).hasSize(1);
    assertThat(distributedSloids.contains("ch:1:sloid:1")).isTrue();
  }

  @Test
  void shouldGetAlreadyDistributedServicePointSloids() {
    // given
    when(sePoDiJdbcTemplate.query(eq("select distinct sloid from service_point_version where sloid is not null;"),
        any(RowMapper.class))).thenReturn(List.of("ch:1:sloid:1"));
    // when
    Set<String> distributedServicePointSloids = sePoDiRepository.getAlreadyDistributedServicePointSloids();
    // then
    assertThat(distributedServicePointSloids).hasSize(1);
    assertThat(distributedServicePointSloids.contains("ch:1:sloid:1")).isTrue();
  }

}
