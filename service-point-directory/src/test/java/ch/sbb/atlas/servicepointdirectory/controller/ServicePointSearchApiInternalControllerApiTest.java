package ch.sbb.atlas.servicepointdirectory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointSearchRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ServicePointSearchApiInternalControllerApiTest extends BaseControllerApiTest {

  private final ServicePointVersionRepository repository;

  @Autowired
  ServicePointSearchApiInternalControllerApiTest(ServicePointVersionRepository repository) {
    this.repository = repository;
  }

  @BeforeEach
  void createDefaultVersion() {
    repository.save(ServicePointTestData.getBernWyleregg());
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
  }

  @Test
  void shouldSearchServicePointSuccessfully() throws Exception {
    // given
    ServicePointSearchRequest request = new ServicePointSearchRequest("bern");
    String jsonString = mapper.writeValueAsString(request);

    // when
    mvc.perform(post("/v1/service-points/search")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].number", is(8589008)))
        .andExpect(jsonPath("$[0].designationOfficial", is("Bern, Wyleregg")));
  }

  @Test
  void whenSearchRequestForSearchSePoWithNetworkTrueValidThenShouldFindServicePointSuccessfully() throws Exception {
    // given
    ServicePointSearchRequest request = new ServicePointSearchRequest("bern");
    String jsonString = mapper.writeValueAsString(request);

    // when
    mvc.perform(post("/v1/service-points/search-sp-with-route-network")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].number", is(8589008)))
        .andExpect(jsonPath("$[0].designationOfficial", is("Bern, Wyleregg")));
  }

  @Test
  void shouldSearchSwissOnlyServicePointSuccessfully() throws Exception {
    // given
    repository.save(ServicePointTestData.createAbroadServicePointVersion());

    ServicePointSearchRequest request = new ServicePointSearchRequest("bern");
    String jsonString = mapper.writeValueAsString(request);

    // when
    mvc.perform(post("/v1/service-points/search-swiss-only")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].number", is(8589008)))
        .andExpect(jsonPath("$[0].designationOfficial", is("Bern, Wyleregg")));
  }

  @Test
  void shouldReturnEmptyListWhenNoMatchFound() throws Exception {
    // given
    ServicePointSearchRequest request = new ServicePointSearchRequest("zug");
    String jsonString = mapper.writeValueAsString(request);

    // when
    mvc.perform(post("/v1/service-points/search")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void whenSearchRequestForSearchSePoWithNetworkTrueValidThenShouldReturnEmptyList() throws Exception {
    // given
    ServicePointSearchRequest request = new ServicePointSearchRequest("zug");
    String jsonString = mapper.writeValueAsString(request);

    // when
    mvc.perform(post("/v1/service-points/search-sp-with-route-network")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void shouldReturnBadRequestWhenSearchWhitLessThanTwoDigit() throws Exception {
    // given
    ServicePointSearchRequest request = new ServicePointSearchRequest("b");
    String jsonString = mapper.writeValueAsString(request);

    // when
    mvc.perform(post("/v1/service-points/search")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details", hasSize(1)))
        .andExpect(jsonPath("$.details.[0].message", is("You must enter at least 2 digits to start a search!")));
  }

  @Test
  void whenSearchRequestForSearchSePoWithNetworkTrueWithLessThanTwoDigitsThenShouldReturnBadRequest() throws Exception {
    // given
    ServicePointSearchRequest request = new ServicePointSearchRequest("b");
    String jsonString = mapper.writeValueAsString(request);

    // when
    mvc.perform(post("/v1/service-points/search-sp-with-route-network")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details", hasSize(1)))
        .andExpect(jsonPath("$.details.[0].message", is("You must enter at least 2 digits to start a search!")));
  }

  @Test
  void whenSearchRequestForSearchSePoWithNetworkTrueNullThenShouldReturnBadRequest() throws Exception {
    // given
    ServicePointSearchRequest request = new ServicePointSearchRequest(null);
    String jsonString = mapper.writeValueAsString(request);

    // when
    mvc.perform(post("/v1/service-points/search-sp-with-route-network")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details", hasSize(1)))
        .andExpect(jsonPath("$.details.[0].message", is("You must enter at least 2 digits to start a search!")));
  }

}
