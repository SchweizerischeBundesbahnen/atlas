package ch.sbb.atlas.location.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.location.SloidType;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

class PrmRepositoryTest {

  @Mock
  private NamedParameterJdbcTemplate prmJdbcTemplate;

  private PrmRepository prmRepository;

  @BeforeEach
  void before() {
    MockitoAnnotations.openMocks(this);
    this.prmRepository = new PrmRepository(prmJdbcTemplate);
  }

  @Test
  void shouldGetAlreadyDistributedSloids() {
    // given
    when(prmJdbcTemplate.query(eq("select distinct sloid from reference_point_version where sloid is not null;"),
        any(RowMapper.class))).thenReturn(
        List.of("ch:1:sloid:1"));
    // when
    Set<String> distributedSloids = prmRepository.getAlreadyDistributedSloids(SloidType.REFERENCE_POINT);
    // then
    assertThat(distributedSloids).hasSize(1);
    assertThat(distributedSloids.contains("ch:1:sloid:1")).isTrue();
  }

}
