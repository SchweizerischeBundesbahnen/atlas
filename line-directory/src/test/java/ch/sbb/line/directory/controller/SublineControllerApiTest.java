package ch.sbb.line.directory.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.api.lidi.LineVersionModel;
import ch.sbb.atlas.api.lidi.SublineVersionModel;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.controller.BaseControllerWithAmazonS3ApiTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.service.export.SublineVersionExportService;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;

class SublineControllerApiTest extends BaseControllerWithAmazonS3ApiTest {

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @Autowired
  private LineController lineController;

  @Autowired
  private SublineController sublineController;

  @Autowired
  private LineVersionRepository lineVersionRepository;

  @Autowired
  private SublineVersionRepository sublineVersionRepository;

  @Autowired
  private SublineVersionExportService sublineVersionExportService;

  @MockBean
  private AmazonService amazonService;

  @AfterEach
  void tearDown() {
    sublineVersionRepository.deleteAll();
    lineVersionRepository.deleteAll();
  }

  @Test
  void shouldReturnNotFoundErrorResponseWhenNoFoundLines() throws Exception {
    //when
    mvc.perform(get("/v1/sublines/versions/123")
            .contentType(contentType))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Entity not found")))
        .andExpect(jsonPath("$.error", is("Not found")))
        .andExpect(jsonPath("$.details[0].message", is("Object with slnid 123 not found")))
        .andExpect(jsonPath("$.details[0].field", is("slnid")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.ENTITY_NOT_FOUND")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("field")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("slnid")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("value")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("123")));
  }

  @Test
  void shouldExportFullSublineVersionsCsv() throws Exception {
    //given
    LineVersionModel lineVersionModel = LineTestData.lineVersionModelBuilder().build();
    lineVersionModel = lineController.createLineVersion(lineVersionModel);
    SublineVersionModel sublineVersionModel1 = SublineTestData.sublineVersionModelBuilder()
        .mainlineSlnid(
            lineVersionModel.getSlnid())
        .build();
    SublineVersionModel sublineVersionModel2 = SublineTestData.sublineVersionModelBuilder()
        .mainlineSlnid(
            lineVersionModel.getSlnid())
        .validFrom(LocalDate.of(2020, 2, 1))
        .validTo(LocalDate.of(2020, 11, 30))
        .description("desc2")
        .build();
    sublineController.createSublineVersion(sublineVersionModel1);
    sublineController.createSublineVersion(sublineVersionModel2);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/sublines/export-csv/full"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  void shouldExportActualSublineVersionsCsv() throws Exception {
    //given
    LineVersionModel lineVersionModel = LineTestData.lineVersionModelBuilder().build();
    lineVersionModel = lineController.createLineVersion(lineVersionModel);
    SublineVersionModel sublineVersionModel1 = SublineTestData.sublineVersionModelBuilder()
        .mainlineSlnid(lineVersionModel.getSlnid())
        .build();
    SublineVersionModel sublineVersionModel2 = SublineTestData.sublineVersionModelBuilder()
        .mainlineSlnid(lineVersionModel.getSlnid())
        .validFrom(LocalDate.of(2020, 2, 1))
        .validTo(LocalDate.of(2020, 11, 1))
        .description("desc2")
        .build();
    sublineController.createSublineVersion(sublineVersionModel1);
    sublineController.createSublineVersion(sublineVersionModel2);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/sublines/export-csv/actual"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  void shouldExportFutureTimetableLineVersionsCsv() throws Exception {
    //given
    LineVersionModel lineVersionModel = LineTestData.lineVersionModelBuilder().build();
    lineVersionModel = lineController.createLineVersion(lineVersionModel);
    SublineVersionModel sublineVersionModel1 = SublineTestData.sublineVersionModelBuilder()
        .mainlineSlnid(
            lineVersionModel.getSlnid())
        .build();
    SublineVersionModel sublineVersionModel2 = SublineTestData.sublineVersionModelBuilder()
        .mainlineSlnid(
            lineVersionModel.getSlnid())
        .validFrom(LocalDate.of(2020, 2, 1))
        .validTo(LocalDate.of(2020, 11, 1))
        .description("desc2")
        .build();
    sublineController.createSublineVersion(sublineVersionModel1);
    sublineController.createSublineVersion(sublineVersionModel2);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/sublines/export-csv/timetable-year-change"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  void shouldRevokeSubline() throws Exception {
    //given
    LineVersionModel lineVersionModel = lineController.createLineVersion(
        LineTestData.lineVersionModelBuilder().build());
    SublineVersionModel sublineVersionSaved = sublineController.createSublineVersion(
        SublineTestData.sublineVersionModelBuilder()
            .mainlineSlnid(lineVersionModel.getSlnid())
            .build());

    //when
    mvc.perform(post("/v1/sublines/" + sublineVersionSaved.getSlnid() + "/revoke")
    ).andExpect(status().isOk());
  }
}
