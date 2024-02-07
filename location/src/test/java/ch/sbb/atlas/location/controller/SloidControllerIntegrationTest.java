package ch.sbb.atlas.location.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.location.LocationSchemaCreation;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@LocationSchemaCreation
class SloidControllerIntegrationTest extends BaseControllerApiTest {

  /** endpoint /generate */
  @Test
  void generateSloid_shouldThrowWhenNothingProvided() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void generateSloid_shouldThrowWhenNoTypeProvided() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON).content("""
            {"sloidPrefix": "ch:1:sloid:1"}"""))
        .andExpect(status().isBadRequest());
  }

  @Test
  void generateSloid_shouldThrowWhenServicePointNoCountry() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON).content("""
            {"sloidType": "SERVICE_POINT"}"""))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")));
  }

  @Test
  void generateSloid_shouldSuccessWhenServicePointRequestValid() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON).content("""
            {"sloidType": "SERVICE_POINT", "country": "SWITZERLAND"}"""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is("ch:1:sloid:1")));
  }

  @Test
  void generateSloid_shouldThrowWhenUnknownTypeProvided() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON).content("""
            {"sloidType": "Test", "sloidPrefix": "ch:1:sloid:1"}"""))
        .andExpect(status().isBadRequest());
  }

  @Test
  void generateSloid_shouldThrowWhenSloidPrefixNotSet() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON).content("""
            {"sloidType": "AREA"}"""))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")));
  }

  @Test
  void generateSloid_shouldThrowWhenSloidPrefixNotValid() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON).content("""
            {"sloidType": "AREA", "sloidPrefix": "ch:1:sloid:test"}"""))
        .andExpect(status().isBadRequest());
  }

  @Test
  void generateSloid_shouldSuccesswWhenAREARequestValid() throws Exception {
    mvc.perform(post("/v1/sloid/generate").contentType(MediaType.APPLICATION_JSON).content("""
            {"sloidType": "AREA", "sloidPrefix": "ch:1:sloid:7000"}"""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is("ch:1:sloid:7000:100")));
  }

  /** endpoint /claim */
  @Test
  void claimSloid_shouldThrowWhenNothingProvided() throws Exception {
    mvc.perform(post("/v1/sloid/claim").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void claimSloid_shouldThrowWhenNoSloidProvided() throws Exception {
    mvc.perform(post("/v1/sloid/claim").contentType(MediaType.APPLICATION_JSON).content("""
            {"sloidType": "SERVICE_POINT", "country": "SWITZERLAND"}"""))
        .andExpect(status().isBadRequest());
  }

  @Test
  void claimSloid_shouldThrowWhenBlankSloidProvided() throws Exception {
    mvc.perform(post("/v1/sloid/claim").contentType(MediaType.APPLICATION_JSON).content("""
            {"sloidType": "SERVICE_POINT", "country": "SWITZERLAND", "sloid": ""}"""))
        .andExpect(status().isBadRequest());
  }

  @Test
  void claimSloid_shouldSuccessWhenSERVICE_POINTRequestValid() throws Exception {
    mvc.perform(post("/v1/sloid/claim").contentType(MediaType.APPLICATION_JSON).content("""
            {"sloidType": "SERVICE_POINT", "sloid": "ch:1:sloid:7000", "country": "SWITZERLAND"}"""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is("ch:1:sloid:7000")));
  }

  @Test
  void claimSloid_shouldSuccessWhenAREARequestValid() throws Exception {
    mvc.perform(post("/v1/sloid/claim").contentType(MediaType.APPLICATION_JSON).content("""
            {"sloidType": "AREA", "sloid": "ch:1:sloid:7000:1"}"""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is("ch:1:sloid:7000:1")));
  }

  @Test
  void claimSloid_shouldThrowWhenSloidOccupied() throws Exception {
    mvc.perform(post("/v1/sloid/claim").contentType(MediaType.APPLICATION_JSON).content("""
            {"sloidType": "AREA", "sloid": "ch:1:sloid:7000:1"}"""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is("ch:1:sloid:7000:1")));

    mvc.perform(post("/v1/sloid/claim").contentType(MediaType.APPLICATION_JSON).content("""
            {"sloidType": "AREA", "sloid": "ch:1:sloid:7000:1"}"""))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message", is("The SLOID ch:1:sloid:7000:1 is already in use.")));
  }

  /** sloid validation */
  @Test
  void claimSloid_shouldThrowWhenNotValidSPSloid() throws Exception {
    mvc.perform(post("/v1/sloid/claim").contentType(MediaType.APPLICATION_JSON).content("""
            {"sloidType": "SERVICE_POINT", "sloid": "ch:1:sloid:7000:1", "country": "SWITZERLAND"}"""))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.message", is("The SLOID ch:1:sloid:7000:1 is not valid due to: did not have 3 colons as expected")));
  }

  @Test
  void claimSloid_shouldThrowWhenNotValidAREASloid() throws Exception {
    mvc.perform(post("/v1/sloid/claim").contentType(MediaType.APPLICATION_JSON).content("""
            {"sloidType": "AREA", "sloid": "ch:1:sloid:7000"}"""))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("The SLOID ch:1:sloid:7000 is not valid due to: did not have 4 colons as expected")));
  }

  @Test
  void claimSloid_shouldThrowWhenNotValidPLATFORMSloid() throws Exception {
    mvc.perform(post("/v1/sloid/claim").contentType(MediaType.APPLICATION_JSON).content("""
            {"sloidType": "PLATFORM", "sloid": "ch:1:sloid:7000:1"}"""))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.message", is("The SLOID ch:1:sloid:7000:1 is not valid due to: did not have 5 colons as expected")));
  }

}
