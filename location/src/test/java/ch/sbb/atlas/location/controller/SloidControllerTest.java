package ch.sbb.atlas.location.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
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
    jdbcTemplate.execute("delete from sloid_allocated;");
    jdbcTemplate.execute("alter sequence area_seq restart with 100;");
    jdbcTemplate.execute("alter sequence edge_seq restart with 1;");
  }

  /** endpoint /generate */
  @Test
  void generateSloid_shouldThrowWhenNothingProvided() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void generateSloid_shouldThrowWhenNoTypeProvided() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON).content("{\"sloidPrefix\": \"ch:1:sloid:1\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void generateSloid_shouldThrowWhenNoPrefixProvided() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON).content("{\"sloidType\": \"AREA\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void generateSloid_shouldThrowWhenUnknownTypeProvided() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON).content("{\"sloidType\": \"Test\","
            + "\"sloidPrefix\": \"ch:1:sloid:1\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void generateSloid_shouldThrowWhenAreaPrefixNotValid() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON)
            .content("{\"sloidType\": \"AREA\",\"sloidPrefix\": \"test\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void generateSloid_shouldReturnNextAvailableSloidForArea() throws Exception {
    jdbcTemplate.execute("insert into sloid_allocated (sloid) values ('ch:1:sloid:7000:100');");
    jdbcTemplate.execute("insert into sloid_allocated (sloid) values ('ch:1:sloid:7000:101');");
    jdbcTemplate.execute("insert into sloid_allocated (sloid) values ('ch:1:sloid:7000:105');");

    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON)
            .content("{\"sloidType\": \"AREA\", \"sloidPrefix\": \"ch:1:sloid:7000\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is("ch:1:sloid:7000:102")));

    String savedSloid = jdbcTemplate.queryForObject("select sloid from sloid_allocated where sloid = ?;",
        String.class, "ch:1:sloid:7000:102");
    assertThat(savedSloid).isNotNull();
  }

  @Test
  void generateSloid_shouldReturnNextAvailableSloidForEdge() throws Exception {
    jdbcTemplate.execute("insert into sloid_allocated (sloid) values ('ch:1:sloid:7000:0:1');");
    jdbcTemplate.execute("insert into sloid_allocated (sloid) values ('ch:1:sloid:7000:0:2');");
    jdbcTemplate.execute("insert into sloid_allocated (sloid) values ('ch:1:sloid:7000:0:5');");

    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON)
            .content("{\"sloidType\": \"EDGE\", \"sloidPrefix\": \"ch:1:sloid:7000\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is("ch:1:sloid:7000:0:3")));

    String savedSloid = jdbcTemplate.queryForObject("select sloid from sloid_allocated where sloid = ?;",
        String.class, "ch:1:sloid:7000:0:3");
    assertThat(savedSloid).isNotNull();
  }

  /** endpoint /claim */
  @Test
  void claimSloid_shouldThrowWhenNothingProvided() throws Exception {
    mvc.perform(post("/v1/sloid/claim").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void claimSloid_shouldThrowWhenSloidNotValid() throws Exception {
    mvc.perform(post("/v1/sloid/claim").contentType(MediaType.APPLICATION_JSON)
            .content("{\"sloid\": \"ch:1:sloid:napoli\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void claimSloid_shouldReturnErrorWhenSloidOccupied() throws Exception {
    jdbcTemplate.execute("insert into sloid_allocated (sloid) values ('ch:1:sloid:7000');");

    mvc.perform(post("/v1/sloid/claim").contentType(MediaType.APPLICATION_JSON)
            .content("{\"sloid\": \"ch:1:sloid:7000\"}"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$", is("ch:1:sloid:7000 is already used.")));
  }

  @Test
  void claimSloid_shouldReturnOkWhenNewSPSloid() throws Exception {
    mvc.perform(post("/v1/sloid/claim").contentType(MediaType.APPLICATION_JSON)
            .content("{\"sloid\": \"ch:1:sloid:7000\"}"))
        .andExpect(status().isOk());

    String savedSloid = jdbcTemplate.queryForObject("select sloid from sloid_allocated where sloid = ?;",
        String.class, "ch:1:sloid:7000");
    assertThat(savedSloid).isNotNull();
  }

  @Test
  void claimSloid_shouldReturnOkWhenNewAreaSloid() throws Exception {
    mvc.perform(post("/v1/sloid/claim").contentType(MediaType.APPLICATION_JSON)
            .content("{\"sloid\": \"ch:1:sloid:7000:500195\"}"))
        .andExpect(status().isOk());

    String savedSloid = jdbcTemplate.queryForObject("select sloid from sloid_allocated where sloid = ?;",
        String.class, "ch:1:sloid:7000:500195");
    assertThat(savedSloid).isNotNull();
  }

  @Test
  void claimSloid_shouldReturnOkWhenNewEdgeSloid() throws Exception {
    mvc.perform(post("/v1/sloid/claim").contentType(MediaType.APPLICATION_JSON)
            .content("{\"sloid\": \"ch:1:sloid:7000:500195:900\"}"))
        .andExpect(status().isOk());

    String savedSloid = jdbcTemplate.queryForObject("select sloid from sloid_allocated where sloid = ?;",
        String.class, "ch:1:sloid:7000:500195:900");
    assertThat(savedSloid).isNotNull();
  }
}
