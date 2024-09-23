package ch.sbb.atlas.servicepointdirectory.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.servicepoint.ServicePointFotCommentModel;
import ch.sbb.atlas.api.servicepoint.ServicePointFotCommentModel.Fields;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointFotCommentRepository;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ServicePointFotCommentControllerApiTest extends BaseControllerApiTest {

  private final ServicePointVersionRepository repository;
  private final ServicePointFotCommentRepository fotCommentRepository;
  private final ServicePointFotCommentController servicePointFotCommentController;
  private ServicePointVersion servicePointVersion;

  @Autowired
  ServicePointFotCommentControllerApiTest(ServicePointVersionRepository repository,
      ServicePointFotCommentRepository fotCommentRepository, ServicePointFotCommentController servicePointFotCommentController) {
    this.repository = repository;
    this.fotCommentRepository = fotCommentRepository;
    this.servicePointFotCommentController = servicePointFotCommentController;
  }

  @BeforeEach
  void createDefaultVersion() {
    servicePointVersion = repository.save(ServicePointTestData.getBernWyleregg());
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
    fotCommentRepository.deleteAll();
  }

  @Test
  void shouldCreateServicePointFotComment() throws Exception {
    ServicePointFotCommentModel fotComment = ServicePointFotCommentModel.builder()
        .fotComment("Very important on demand service point")
        .build();

    mvc.perform(put("/v1/service-points/" + servicePointVersion.getNumber().getValue() + "/fot-comment")
            .contentType(contentType)
            .content(mapper.writeValueAsString(fotComment)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.fotComment, is("Very important on demand service point")));
  }

  @Test
  void shouldGetServicePointFotComment() throws Exception {
    ServicePointFotCommentModel fotComment = ServicePointFotCommentModel.builder()
        .fotComment("Very important on demand service point")
        .build();

    servicePointFotCommentController.saveFotComment(servicePointVersion.getNumber().getValue(), fotComment);

    mvc.perform(get("/v1/service-points/" + servicePointVersion.getNumber().getValue() + "/fot-comment"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.fotComment, is("Very important on demand service point")));
  }


}
