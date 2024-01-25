package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointGeoDataApiV1;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

 class ServicePointGeoDataApiTest extends BaseControllerApiTest {

  private final ServicePointVersionRepository repository;
  private final ServicePointGeoDataController geoDataController;

  @Autowired
   ServicePointGeoDataApiTest(ServicePointVersionRepository repository, ServicePointGeoDataController geoDataController) {
    this.repository = repository;
    this.geoDataController = geoDataController;
  }

  @BeforeEach
  void setUp() {
    repository.save(ServicePointTestData.getBernWyleregg());

    mvc = MockMvcBuilders.standaloneSetup(geoDataController).setMessageConverters(new ProtobufHttpMessageConverter()).build();
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
  }

  @Test
  void shouldGetTileWithWyleregg() throws Exception {
    int z = 14;
    int x = 8530;
    int y = 5765;

    MvcResult result = mvc.perform(get("/v1/service-points/geodata/" + z + "/" + x + "/" + y + ".pbf?validAtDate=2021-03-31"))
        .andExpect(status().isOk()).andReturn();

    assertThat(result.getResponse().getContentType()).isEqualTo(ServicePointGeoDataApiV1.MEDIA_TYPE_PROTOBUF);
    assertThat(result.getResponse().getContentAsString()).contains("service-points", "number");
  }

}