package ch.sbb.atlas.location.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

class SloidControllerTest extends BaseControllerApiTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setUp() {
    jdbcTemplate.execute("alter sequence service_point_sloid_seq restart with 1;");
    jdbcTemplate.execute("delete from SERVICE_POINT_SLOID_ALLOCATED;");
  }

  @Test
  void generateSloid_shouldThrowWhenNoTypeProvided() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void generateSloid_shouldThrowWhenUnknownTypeProvided() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON).content("{\"sloidType\": \"Test\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void generateSloid_shouldReturnWhenKnownTypeProvided() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON).content("{\"sloidType\": \"SERVICE_POINT\"}"))
        .andExpect(status().isOk());
  }

  @Test
  void generateSloid_shouldReturnNextAvailableSloidWhenNothingAllocated() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON).content("{\"sloidType\": \"SERVICE_POINT\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is("ch:1:sloid:1")));
  }

  @Test
  void generateSloid_shouldIncrementSequenceByOneWhenNothingAllocated() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON).content("{\"sloidType\": \"SERVICE_POINT\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is("ch:1:sloid:1")));

    assertThat(jdbcTemplate.queryForObject("select nextval(?);", Long.class, "service_point_sloid_seq")).isEqualTo(2);
  }

  @Test
  void generateSloid_shouldAllocateReturnedValue() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON).content("{\"sloidType\": \"SERVICE_POINT\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is("ch:1:sloid:1")));

    List<Integer> allocatedNumberEntities = jdbcTemplate.queryForList("select * from service_point_sloid_allocated;",
        Integer.class);
    assertThat(allocatedNumberEntities).hasSize(1);
    assertThat(allocatedNumberEntities.get(0)).isEqualTo(1);
  }

  @Test
  void generateSloid_shouldIncrementSequenceUntilNextFreeNumber_whenNextValueIsAllocated() throws Exception {
    jdbcTemplate.execute("insert into SERVICE_POINT_SLOID_ALLOCATED (NUMBER) values (1);");

    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON).content("{\"sloidType\": \"SERVICE_POINT\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is("ch:1:sloid:2")));

    List<Integer> allocatedNumberEntities = jdbcTemplate.queryForList("select * from service_point_sloid_allocated;",
        Integer.class);
    assertThat(allocatedNumberEntities).hasSize(2);

    assertThat(jdbcTemplate.queryForObject("select nextval(?);", Long.class, "service_point_sloid_seq")).isEqualTo(3);
  }

  // todo: test thread-safety (lock allocated table with db transactions), test with manual sloid endpoint

}
